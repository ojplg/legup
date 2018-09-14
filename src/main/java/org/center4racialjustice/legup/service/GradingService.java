package org.center4racialjustice.legup.service;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import org.center4racialjustice.legup.db.BillActionDao;
import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.ConnectionWrapper;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.db.ReportCardDao;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.Grade;
import org.center4racialjustice.legup.domain.GradeCalculator;
import org.center4racialjustice.legup.domain.Grader;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.util.LookupTable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GradingService {

    private final ConnectionPool connectionPool;

    public GradingService(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public LookupTable<Legislator, Bill, Integer> calculate(long reportCardId) {
        try (ConnectionWrapper connection = connectionPool.getWrappedConnection()) {

            ReportCardDao reportCardDao = new ReportCardDao(connection);
            LegislatorDao legislatorDao = new LegislatorDao(connection);
            BillDao billDao = new BillDao(connection);
            BillActionDao billActionDao = new BillActionDao(connection);

            ReportCard reportCard = reportCardDao.read(reportCardId);

            long session = reportCard.getSessionNumber();

            List<Legislator> legislators = legislatorDao.readBySession(session);

            GradeCalculator calculator = new GradeCalculator(reportCard, legislators);
            List<Long> billIds = reportCard.getReportFactors().stream()
                    .map(rf -> rf.getBill().getId()).collect(Collectors.toList());

            List<Bill> bills = billDao.readByIds(billIds);
            Map<Bill, List<BillAction>> votesByBill = new HashMap<>();
            for (Bill bill : bills) {
                List<BillAction> billActions = billActionDao.readByBill(bill);
                votesByBill.put(bill, billActions);
            }

            return calculator.calculate(votesByBill);
        }
    }

    public static Map<Legislator, Grade> assignGrades(LookupTable<Legislator, Bill, Integer> scores){
        Map<Legislator, Integer> sums = sumScores(scores);
        Integer min = findMin(sums);
        Integer max = findMax(sums);
        Grader grader = new Grader(min, max);

        Map<Legislator, Grade> grades = new HashMap<>();
        for( Map.Entry<Legislator, Integer> entry : sums.entrySet()){
            Grade grade = grader.assignGrade(entry.getValue());
            grades.put(entry.getKey(), grade);
        }

        return grades;
    }

    public static Multimap<Grade, Legislator> collectByGrades(Map<Legislator, Grade> grades){
        Multimap<Grade, Legislator> collated = MultimapBuilder.hashKeys().arrayListValues().build();
        for( Map.Entry<Legislator, Grade> entry : grades.entrySet()){
            collated.put(entry.getValue(), entry.getKey());
        }
        return collated;
    }

    private static Map<Legislator, Integer> sumScores(LookupTable<Legislator, Bill, Integer> scores) {
        Map<Legislator, Integer> sums = new HashMap<>();

        for (Legislator legislator : scores.getRowHeadings()) {
            Integer sum = scores.computeRowSummary(legislator, 0, GradeCalculator.ScoreComputer);
            sums.put(legislator, sum);
        }

        return sums;
    }

    private static Integer findMax(Map<Legislator, Integer> sums){
        Integer max = Integer.MIN_VALUE;
        for(Integer amount : sums.values()){
            if (amount > max){
                max = amount;
            }
        }
        return max;
    }

    private static Integer findMin(Map<Legislator, Integer> sums){
        Integer min = Integer.MAX_VALUE;
        for(Integer amount : sums.values()){
            if (amount < min){
                min = amount;
            }
        }
        return min;
    }

}

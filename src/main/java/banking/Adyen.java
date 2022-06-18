package banking;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Adyen {

    public static void main(String[] args) throws IOException {

        try (final Scanner scanner = new Scanner(System.in)) {
            List<Result.BinRange> binRanges = new ArrayList<>();

            String cardNumber = scanner.next();
            scanner.nextLine();

            scanner.useDelimiter("[,\n]");

            while (scanner.hasNext()) {
                String start = scanner.next();
                String end = scanner.next();
                String cardType = scanner.next();
                binRanges.add(new Result.BinRange(start, end, cardType));
                if (scanner.hasNextLine()) {
                    scanner.nextLine();
                }
            }

            Result.CardTypeCache cache = Result.buildCache(binRanges);
            if (cache != null) {
                System.out.println(cache.get(cardNumber));
            }
        }
    }

    class Result {

        /**
         * An entity to hold bin range details. A bin range is a pair of 12 digit numbers that
         * mark the boundaries of the range which is maped to other bin range properties such
         * as a card type. The range boundaries are inclusive.
         */
        static final class BinRange {
            final String start;
            final String end;
            final String cardType;

            BinRange(String start, String end, String cardType) {
                this.start = start;
                this.end = end;
                this.cardType = cardType;
            }
            public Long getStart() {
                return Long.valueOf(start);
            }
            public Long getEnd() {
                return Long.valueOf(end);
            }
            public String getCardType() {
                return cardType;
            }
        }

        interface CardTypeCache {
            /**
             * @param cardNumber 12 to 23 digit card number.
             *
             * @return the card type for this cardNumber or null if the card number does not
             *      fall into any valid bin ranges.
             */
            String get(String cardNumber);
        }

        /**
         * @param binRanges the list of card bin ranges to build a cache from.
         *
         * @return an implementation of CardTypeCache.
         */
        public static CardTypeCache buildCache(List<BinRange> binRanges) {
            TreeMap<Long, String> ranges = new TreeMap<>();

            binRanges = binRanges.stream().sorted(Comparator.comparing(BinRange::getStart)).collect(Collectors.toList());
            for (int i = 0; i < binRanges.size(); i++) {
                BinRange range = binRanges.get(i);
                ranges.put(range.getStart(), range.getCardType());
                ranges.put(range.getEnd() + 1L, null);
            }

            return new TaskCardTypeCache(ranges);
        }

        static final class TaskCardTypeCache implements CardTypeCache {
            private TreeMap<Long, String> ranges;
            private static final int BIN_SIZE = 12;

            public TaskCardTypeCache(TreeMap<Long, String> ranges) {
                this.ranges = ranges;
            }

            public String get(String cardNumber) {
                System.out.println("***" + cardNumber);
                System.out.println("***" + ranges);
                Long number = Long.parseLong(cardNumber.substring(0, BIN_SIZE));
                String cardType = ranges.get(number);
                return cardType == null ? ranges.ceilingEntry(number).getValue() : cardType;
            }
        }

    }
}

//SET @i = 13;
//        select merchantName, hashedShopperReference, risk, @i:=@i-1 AS iterator from
//        (
//        select merchantName, hashedShopperReference, round(avg(riskScore * quantity) / sum(quantity), 2) as risk from
//        (
//        select mr.merchantName, tr.hashedShopperReference, tr.riskScore, tr.quantity from transaction tr
//        join merchant mr on tr.merchantId = mr.merchantId
//        ) res group by merchantName, hashedShopperReference order by merchantName ASC, risk DESC
//        ) blabla;

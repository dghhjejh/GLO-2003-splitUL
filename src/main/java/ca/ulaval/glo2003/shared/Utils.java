package ca.ulaval.glo2003.shared;

import jakarta.ws.rs.BadRequestException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Utils {

    public static double roundAmount(double amount) {
        if (Double.isInfinite(amount) || Double.isNaN(amount)) {
            throw new IllegalArgumentException("Amount must be a finite number");
        }
        BigDecimal amout = BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
        return amout.doubleValue();
    }

    public static void verifyPurchaseDate(String purchaseDate) {
        LocalDate parsedPurchaseDate = LocalDate.parse(purchaseDate, DateTimeFormatter.ISO_LOCAL_DATE);
        if (parsedPurchaseDate.isAfter(LocalDate.now())) throw new BadRequestException(
            "La date d'achat est apres la date courante"
        );
    }
}

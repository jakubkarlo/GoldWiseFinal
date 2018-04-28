package jakubkarlo.com.goldwise.Models;

/**
 * Created by Jakub on 11.01.2018.
 */

public class DebtState {

    // It's not an enum as parse servers parses it wrongly
    public static final int DEBT_NOT_PAID = 1;
    public static final int DEBT_PAID = 2;
    public static final int DEBT_FOR_SAVING = 3;

}

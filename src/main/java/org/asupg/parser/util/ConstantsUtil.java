package org.asupg.parser.util;

import org.apache.poi.ss.util.CellReference;

public class ConstantsUtil {

    public static final int START_ROW = 17;
    public static final int DATE_COLUMN = column("F");
    public static final int DOC_NUM_COLUMN = column("J");
    public static final int ACCOUNT_NAME_COLUMN = column("P");
    public static final int ACCOUNT_NUM_COLUMN = column("Y");
    public static final int MFO_COLUMN = column("AF");
    public static final int DEBIT_COLUMN = column("AG");
    public static final int CREDIT_COLUMN = column("AK");
    public static final int DESCRIPTION_COLUMN = column("AM");

    public static int column(String columnName) {
        return CellReference.convertColStringToIndex(columnName);
    }
}

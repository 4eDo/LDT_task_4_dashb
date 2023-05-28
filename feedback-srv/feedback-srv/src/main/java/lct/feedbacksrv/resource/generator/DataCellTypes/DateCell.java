package lct.feedbacksrv.resource.generator.DataCellTypes;

import lct.feedbacksrv.resource.generator.DataCell;
import org.apache.poi.ss.usermodel.Cell;

import java.util.Date;

/**
 * Date cell
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */
public class DateCell extends DataCell<Date> {
    public DateCell(Date value) {
        super(value);
    }

    @Override
    public void writeCell(Cell cell) {
        cell.setCellValue(value);
    }
}

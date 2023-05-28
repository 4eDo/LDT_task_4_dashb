package lct.feedbacksrv.resource.generator.DataCellTypes;

import lct.feedbacksrv.resource.Utils;
import lct.feedbacksrv.resource.generator.DataCell;
import org.apache.poi.ss.usermodel.Cell;

/**
 * Double cell
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */
public class DoubleCell extends DataCell<Double> {
    public DoubleCell(Double value) {
        super(Utils.roundStringNumber(String.valueOf(value)));
    }

    @Override
    public void writeCell(Cell cell) {
        cell.setCellValue(value);
    }
}

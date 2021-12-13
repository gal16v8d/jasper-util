package com.gsdd.jasper.util.exception;

import com.gsdd.constants.JasperConstants;

/**
 * 
 * @author Great System Development Dynamic (<b>GSDD</b>) <br>
 *         Alexander Galvis Grisales <br>
 *         alex.galvis.sistemas@gmail.com <br>
 */
public class ReportFilePathException extends Exception {

    private static final long serialVersionUID = -5522782110087130068L;

    public ReportFilePathException() {
        super(JasperConstants.ERROR_JASPER_REPORT_PATH);
    }

}

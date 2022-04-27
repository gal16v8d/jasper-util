package com.gsdd.jasper.util.exception;

import com.gsdd.constants.JasperConstants;

/**
 * @author Great System Development Dynamic (<b>GSDD</b>) <br>
 *     Alexander Galvis Grisales <br>
 *     alex.galvis.sistemas@gmail.com <br>
 */
public class OutFilePathException extends Exception {

    private static final long serialVersionUID = 3628475488779441649L;

    public OutFilePathException() {
        super(JasperConstants.ERROR_JASPER_OUTPUT_PATH);
    }
}

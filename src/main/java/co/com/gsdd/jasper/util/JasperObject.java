package co.com.gsdd.jasper.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class JasperObject {
    /**
     * list with data to show.
     */
    private List<?> dataList;
    /**
     * parameters input for the repot.
     */
    private Map<String, Object> parameters;
    /**
     * path for report to generate.
     */
    private String reportFilePath;
    /**
     * path for store the generated report.
     */
    private String reportOutPath;
    /**
     * Error message tgo show.
     */
    private String message;

    public JasperObject(List<?> dataList, String reportFilePath, Map<String, Object> parameters, String reportOutPath) {
        this.dataList = dataList;
        this.reportFilePath = reportFilePath;
        this.parameters = parameters;
        this.reportOutPath = reportOutPath;
    }

    public JasperObject(List<?> dataList, String reportFilePath, String reportOutPath) {
        this(dataList, reportFilePath, new HashMap<>(), reportOutPath);
    }

    public JasperObject(List<?> dataList) {
        this.dataList = dataList;
    }

    public JasperObject(List<?> dataList, Map<String, Object> parameters) {
        this.dataList = dataList;
        this.parameters = parameters;
    }

}

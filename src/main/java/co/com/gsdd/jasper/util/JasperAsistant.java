package co.com.gsdd.jasper.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import co.com.gsdd.constants.JasperConstants;
import co.com.gsdd.exception.TechnicalException;
import co.com.gsdd.jasper.util.exception.OutFilePathException;
import co.com.gsdd.jasper.util.exception.ReportFilePathException;
import co.com.gsdd.validatorutil.ValidatorUtil;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;

/**
 * 
 * @author Great System Development Dynamic (<b>GSDD</b>) <br>
 *         Alexander Galvis Grisales <br>
 *         alex.galvis.sistemas@gmail.com <br>
 */
public class JasperAsistant {

	private JasperReport jasperReport;
	private JasperPrint jasperPrint;
	private JRBeanCollectionDataSource dataSource;

	public boolean generatePdfReport(JasperObject jasperObject) {
		GenerateReport report = (JasperObject jasperObj, JasperReport jasperReport,
				JRBeanCollectionDataSource dataSource) -> {
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, jasperObj.getParameters(), dataSource);
			JasperExportManager.exportReportToPdfFile(jasperPrint, jasperObj.getReportOutPath());
			return true;
		};
		return report.generateReport(jasperObject);
	}

	public boolean createWordReport(JasperObject jasperObject) {
		GenerateReport report = (JasperObject jasperObj, JasperReport jasperReport,
				JRBeanCollectionDataSource dataSource) -> {
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, jasperObject.getParameters(),
					dataSource);
			JRDocxExporter exporter = new JRDocxExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exporter.setParameter(JRExporterParameter.OUTPUT_FILE, new File(jasperObject.getReportOutPath()));
			exporter.exportReport();
			return true;
		};
		return report.generateReport(jasperObject);
	}

	public boolean createXlsReport(JasperObject jasperObject) {
		GenerateReport report = (JasperObject jasperObj, JasperReport jasperReport,
				JRBeanCollectionDataSource dataSource) -> {
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, jasperObject.getParameters(),
					dataSource);
			JRXlsxExporter exporter = new JRXlsxExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exporter.setParameter(JRExporterParameter.OUTPUT_FILE, new File(jasperObject.getReportOutPath()));
			exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);
			exporter.exportReport();
			return true;
		};
		return report.generateReport(jasperObject);
	}

	public byte[] createPdfReportBytes(JasperObject objectJ) {
		InputStream report = null;
		try {
			validateJasperObject(objectJ);
			report = getClass().getResourceAsStream(objectJ.getReportFilePath());
			this.jasperReport = JasperCompileManager.compileReport(report);
			this.dataSource = new JRBeanCollectionDataSource(objectJ.getDataList());
			return JasperRunManager.runReportToPdf(this.jasperReport, objectJ.getParameters(), this.dataSource);
		} catch (ReportFilePathException | OutFilePathException rfpe) {
			return null;
		} catch (Exception e) {
			throw new TechnicalException(e);
		} finally {
			closeQuietly(report);
		}
	}

	public byte[] createXlsReportBytes(JasperObject objectJ) {
		ByteArrayOutputStream baos = null;
		InputStream report = null;
		try {
			validateJasperObject(objectJ);
			report = getClass().getResourceAsStream(objectJ.getReportFilePath());
			this.jasperReport = JasperCompileManager.compileReport(report);
			this.dataSource = new JRBeanCollectionDataSource(objectJ.getDataList());
			this.jasperPrint = JasperFillManager.fillReport(this.jasperReport, objectJ.getParameters(),
					this.dataSource);
			baos = new ByteArrayOutputStream();
			JRXlsxExporter exporter = new JRXlsxExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, this.jasperPrint);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
			exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);
			exporter.exportReport();
			return baos.toByteArray();
		} catch (ReportFilePathException | OutFilePathException rfpe) {
			return null;
		} catch (Exception e) {
			throw new TechnicalException(e);
		} finally {
			closeQuietly(report);
			closeQuietly(baos);
		}
	}

	/**
	 * @param objectJ     report properties.
	 * @param response    servlet response.
	 * @param contentType content type to return.
	 * @param bytes       bytes to build the report.
	 */
	private void seeWebReport(JasperObject objectJ, HttpServletResponse response, String contentType, byte[] bytes) {
		ServletOutputStream outputStream = null;
		try {
			if (ValidatorUtil.isNullOrEmpty(objectJ.getReportFilePath())) {
				throw new ReportFilePathException();
			}
			outputStream = response.getOutputStream();
			response.setContentLength(bytes.length);
			response.setContentType(contentType);
			response.addHeader(JasperConstants.CONTENT_DISPOSITION,
					JasperConstants.ATTACHMENT_FILENAME + objectJ.getReportOutPath());
			outputStream.write(bytes, 0, bytes.length);
			outputStream.flush();
		} catch (Exception e) {
			throw new TechnicalException(e);
		} finally {
			closeQuietly(outputStream);
		}
	}

	public void seePdfWebReport(JasperObject objectJ, HttpServletResponse response) {
		byte[] bytespdf = createPdfReportBytes(objectJ);
		seeWebReport(objectJ, response, JasperConstants.APPLICATION_PDF, bytespdf);
	}

	public void seeXlsWebReport(JasperObject objectJ, HttpServletResponse response) {
		byte[] bytes = createXlsReportBytes(objectJ);
		seeWebReport(objectJ, response, JasperConstants.APPLICATION_XLS, bytes);
	}

	/**
	 * 
	 * @param objectJ
	 * @throws ReportFilePathException if report file path is not defined.
	 * @throws OutFilePathException    if output file path is not defined.
	 */
	private static void validateJasperObject(JasperObject objectJ)
			throws ReportFilePathException, OutFilePathException {
		if (ValidatorUtil.isNullOrEmpty(objectJ.getReportFilePath())) {
			throw new ReportFilePathException();
		}

		if (ValidatorUtil.isNullOrEmpty(objectJ.getReportOutPath())) {
			throw new OutFilePathException();
		}

		if (objectJ.getParameters() == null) {
			objectJ.setParameters(new HashMap<>());
		}

		if (objectJ.getDataList() == null) {
			objectJ.setDataList(new ArrayList<>());
		}
	}

	private static void closeQuietly(Closeable resource) {
		if (resource != null) {
			try {
				resource.close();
			} catch (IOException e) {
				// NOSONAR
			}
		}
	}

	public interface GenerateReport {

		default boolean generateReport(JasperObject jasperObject) {
			InputStream report = null;
			try {
				validateJasperObject(jasperObject);
				report = getClass().getResourceAsStream(jasperObject.getReportFilePath());
				JasperReport jasperReport = JasperCompileManager.compileReport(report);
				JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(jasperObject.getDataList());
				return executeReportAction(jasperObject, jasperReport, dataSource);
			} catch (ReportFilePathException | OutFilePathException rfpe) {
				return false;
			} catch (Exception e) {
				throw new TechnicalException(e);
			} finally {
				closeQuietly(report);
			}
		}

		boolean executeReportAction(JasperObject jasperObject, JasperReport jasperReport,
				JRBeanCollectionDataSource dataSource) throws Exception;

	}

}

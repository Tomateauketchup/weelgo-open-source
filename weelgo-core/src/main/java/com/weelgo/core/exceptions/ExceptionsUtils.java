package com.weelgo.core.exceptions;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weelgo.core.CoreUtils;
import com.weelgo.core.MessageManager;

public class ExceptionsUtils {

	private static Logger logger = LoggerFactory.getLogger(ExceptionsUtils.class);

	public static void logException(Throwable e, Logger logger) {
		if (logger != null && e != null) {
			logger.error(getMessagesFromException(e));
		}
	}

	public static String getMessagesFromException(Throwable e) {
		String strTemp = "";

		strTemp = e.toString();
		for (int i = 0; i < e.getStackTrace().length; i++) {
			strTemp = strTemp + "\r\n at " + e.getStackTrace()[i];
		}
		strTemp = strTemp + "\r\n\r\n";
		return strTemp;
	}

	public static boolean isThisException(Exception e, String type) {
		if (type != null && !type.isEmpty() && e instanceof WeelgoException
				&& type.equals(((WeelgoException) e).getType()))
			return true;

		return false;
	}

	public static void throwDynamicException(String type) throws WeelgoException {
		throwDynamicException(type, null, null, null, null);
	}

	public static void throwDynamicException(String type, String message) throws WeelgoException {
		throwDynamicException(type, message, null, null, null);
	}

	public static void throwDynamicExceptionInvalidInput() throws WeelgoException {
		throwDynamicException(WeelgoException.INVALID_INPUT, null, null, null, null);
	}

	public static void throwDynamicException(String type, String message, Locale locale, String[] messageParameters,
			MessageManager messageManager) throws WeelgoException {
		String str = getMessage(type, message, locale, messageParameters, messageManager);
		throw new WeelgoDynamicException(str, type);
	}

	public static void throwException(String message) {
		try {
			throw new Exception(message);
		} catch (Exception e) {
			ManageException(e, logger);
		}

	}

	private static String getMessage(String type, String message, Locale locale, String[] messageParameters,
			MessageManager messageManager) {
		String str = "";
		if (CoreUtils.isNotNullOrEmpty(message)) {
			str = message + " -> ";
		}
		if (messageManager != null && locale != null && CoreUtils.isNotNullOrEmpty(type)) {
			String strTmp = messageManager.getMessage(getMessageCode(type), messageParameters, locale);
			if (CoreUtils.isNotNullOrEmpty(strTmp)) {
				str = str + strTmp;
			}
		}
		str = CoreUtils.removeEnd(str, " -> ");
		return str;
	}

	private static void throwFatalException(Throwable e) throws WeelgoException {
		throw new WeelgoFatalException(e, WeelgoException.FATAL_EXTERNAL);
	}

	private static void throwFatalException(String type, String message, Locale locale, String[] messageParameters,
			MessageManager messageManager) throws WeelgoException {
		String str = getMessage(type, message, locale, messageParameters, messageManager);
		throw new WeelgoFatalException(str, type);
	}

	public static String getMessageCode(String type) {
		return "exception." + type;
	}

	public static void ManageException(Throwable e, Logger logger) throws WeelgoException {
		ManageException("", e, logger);
	}

	public static void ManageException(String extraMessage, Throwable e, Logger logger) throws WeelgoException {
		if (e != null) {
			if (e instanceof WeelgoException)
				throw (WeelgoException) e;
			else {
				if (logger != null) {
					if (extraMessage == null) {
						extraMessage = "";
					}
					if (!extraMessage.isEmpty()) {
						extraMessage = extraMessage + " -> ";
					}
					logger.error(extraMessage + ExceptionsUtils.getMessagesFromException(e));
				}
				throwFatalException(e);
			}

		}
	}
}
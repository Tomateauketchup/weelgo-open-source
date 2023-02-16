package com.weelgo.core;

import java.util.Locale;

public interface MessageManager {

	public String getMessage(String key, Object[] param, String defaultMessage, Locale locale);
	public String getMessage(String key, Object[] param, Locale locale);
	public String getMessage(String key, Object[] param);
	public String getMessage(String key, Locale locale);
	public String getMessage(String key);
	public void setLocale(Locale locale);
	public void setDefaultLocale(Locale locale);

}
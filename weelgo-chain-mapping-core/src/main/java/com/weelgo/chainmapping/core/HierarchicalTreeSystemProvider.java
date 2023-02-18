package com.weelgo.chainmapping.core;

public interface HierarchicalTreeSystemProvider extends HierarchicalTreeSystemNavProvider {

	public <T> T deserializeJsonFile(Object file, Class<T> valueType);

	public void serialiseInJsonFile(Object fileObject, Object objectToSerialize);

	public boolean isFolderExist(Object folder);

	public boolean isFileExist(Object file);

	public Object getFile(Object parentFolder, String fileName);
	
	public Object getFolder(Object parentFolder, String name);

	public default Object createFolder(Object parentFolder) {
		return createFolder(parentFolder, null);
	}

	public abstract Object createFolder(Object parentFolder, String folderName);
	
	public String getUniqueIdForFolderOrFile(Object o);
	
	public boolean isFileSystemElement(Object o);

	public void deleteFolder(Object folderToTest);

}

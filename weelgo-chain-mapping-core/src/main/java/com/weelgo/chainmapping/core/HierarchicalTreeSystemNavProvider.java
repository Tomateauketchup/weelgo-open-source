package com.weelgo.chainmapping.core;

import java.util.List;

public interface HierarchicalTreeSystemNavProvider {	

	public Object getParentFolder(Object child);	

	public List<Object> getChildFolders(Object folder);	

	public boolean isSameFolder(Object folder1,Object folder2);
	
	public String getName(Object o);
	
	public boolean isFile(Object o);
	
	public boolean isFolder(Object o);

}

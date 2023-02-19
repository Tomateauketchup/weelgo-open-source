package com.weelgo.eclipse.plugin;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weelgo.chainmapping.core.HierarchicalTreeSystemProvider;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.exceptions.ExceptionsUtils;
import com.weelgo.core.json.JsonUtils;

public class EclipseHierarchicalTreeSystemProvider implements HierarchicalTreeSystemProvider {
	private static Logger logger = LoggerFactory.getLogger(EclipseHierarchicalTreeSystemProvider.class);

	@Override
	public boolean isFileSystemElement(Object o) {
		return o instanceof IResource;
	}

	@Override
	public String getUniqueIdForFolderOrFile(Object o) {
		if (o instanceof IResource) {
			IResource r = (IResource) o;
			return r.getFullPath().toString();
		}
		return null;
	}

	@Override
	public Object getParentFolder(Object child) {
		if (child instanceof IContainer) {
			IContainer p = (IContainer) child;
			return p.getParent();
		} else if (child instanceof IFile) {
			IFile p = (IFile) child;
			return p.getParent();
		}
		return null;
	}

	@Override
	public List<Object> getChildFolders(Object folder) {
		try {
			if (isFolderExist(folder)) {
				if (folder instanceof IContainer) {
					IContainer p = (IContainer) folder;
					IResource[] childs = p.members();
					ArrayList<Object> arl = new ArrayList<>();
					if (childs != null) {
						for (IResource iResource : childs) {
							if (iResource instanceof IContainer && iResource.exists() && isFolder(iResource)) {
								arl.add(iResource);
							}
						}
					}
					return arl;
				}
			}

		} catch (Exception e) {
			ExceptionsUtils.ManageException(e, logger);
		}

		return null;
	}

	@Override
	public boolean isSameFolder(Object folder1, Object folder2) {
		if (folder1 != null && folder2 != null) {
			if (folder1 == folder2) {
				return true;
			}
			if (folder1 instanceof IContainer && folder2 instanceof IContainer) {
				IContainer f1 = (IContainer) folder1;
				IContainer f2 = (IContainer) folder2;
				return f1.getFullPath().equals(f2.getFullPath());
			}
		}
		return false;
	}

	@Override
	public <T> T deserializeJsonFile(Object file, Class<T> valueType) {
		if (file instanceof IFile) {
			IFile cf = (IFile) file;
			File f = new File(cf.getLocationURI());
			try {
				return JsonUtils.deserializeJsonFile(f, valueType);
			} catch (Exception e) {
				ExceptionsUtils.ManageException(e, logger);
			}

		}
		return null;
	}

	@Override
	public void serialiseInJsonFile(Object fileObject, Object objectToSerialize) {
		try {
			if (fileObject instanceof IFile) {
				IFile f = (IFile) fileObject;
				ByteArrayInputStream is = new ByteArrayInputStream(
						JsonUtils.serializeIntoJsonByteArray(objectToSerialize, true));
				if (f.exists()) {
					f.setContents(is, true, false, null);
				} else {
					f.create(is, true, null);
				}

			}
		} catch (Exception e) {
			ExceptionsUtils.ManageException(e, logger);
		}

	}

	@Override
	public boolean isFolderExist(Object folder) {
		if (folder instanceof IContainer) {
			IContainer p = (IContainer) folder;
			return p.exists();
		}
		return false;
	}

	@Override
	public String getName(Object o) {
		if (o instanceof IResource) {
			IResource r = (IResource) o;
			return r.getName();
		}
		return null;
	}

	@Override
	public boolean isFile(Object o) {
		if (o instanceof IFile) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isFolder(Object o) {
		if (o instanceof IResource) {
			IResource iResource = (IResource) o;
			return iResource.getType() == IResource.FOLDER || iResource.getType() == IResource.PROJECT
					|| iResource.getType() == IResource.ROOT;
		}
		return false;
	}

	@Override
	public boolean isFileExist(Object file) {
		if (file instanceof IFile) {
			IFile cf = (IFile) file;
			return cf.exists();
		}
		return false;
	}

	@Override
	public Object getFile(Object parentFolder, String fileName) {
		if (parentFolder instanceof IContainer) {
			IContainer p = (IContainer) parentFolder;

			if (p.getType() == IResource.ROOT) {
				return null;
			}
			return p.getFile(new Path(fileName));
		}
		return null;

	}

	@Override
	public Object getFolder(Object parentFolder, String name) {
		if (parentFolder instanceof IContainer) {
			IFolder f = null;

			IContainer p = (IContainer) parentFolder;
			f = p.getFolder(new Path(name));
			return f;
		}
		return null;
	}

	@Override
	public Object createFolder(Object parentFolder, String folderName) {

		try {
			if (CoreUtils.isNotNullOrEmpty(folderName)) {
				parentFolder = getFolder(parentFolder, folderName);
			}
			if (parentFolder != null && parentFolder instanceof IFolder) {
				IFolder f = (IFolder) parentFolder;
				if (f.exists() == false) {
					f.create(true, true, null);
				}
				return f;
			}
		} catch (Exception e) {
			ExceptionsUtils.ManageException(e, logger);
		}
		return null;
	}

	@Override
	public void deleteFolder(Object folderToTest) {
		try {
			if (folderToTest != null && folderToTest instanceof IContainer) {
				IContainer c = (IContainer) folderToTest;
				c.delete(true, null);
			}
		} catch (Exception e) {
			ExceptionsUtils.ManageException(e, logger);
		}

	}

}

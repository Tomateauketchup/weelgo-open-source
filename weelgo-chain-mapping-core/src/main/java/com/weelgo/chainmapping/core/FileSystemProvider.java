package com.weelgo.chainmapping.core;

import static com.weelgo.core.CoreUtils.isNotNullOrEmpty;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weelgo.core.CoreUtils;
import com.weelgo.core.exceptions.ExceptionsUtils;
import com.weelgo.core.json.JsonUtils;

public class FileSystemProvider implements HierarchicalTreeSystemProvider {

	private static Logger logger = LoggerFactory.getLogger(FileSystemProvider.class);

	@Override
	public Object getFolderFromFullPath(String... path) {

		if (path != null) {
			File f = null;
			for (String str : path) {
				if (CoreUtils.isNotNullOrEmpty(str)) {
					if (f == null) {
						f = new File(str);
					} else {
						f = new File(f, str);
					}
				}
			}
			return f;
		}
		return null;
	}

	@Override
	public <T> T deserializeJsonFile(Object file, Class<T> valueType) {
		try {
			if (file instanceof File) {
				File f = (File) file;
				return JsonUtils.deserializeJsonFile(f, valueType);
			}
		} catch (Exception e) {
			ExceptionsUtils.ManageException(e, logger);
		}
		return null;
	}

	@Override
	public void serialiseInJsonFile(Object fileObject, Object objectToSerialize) {
		try {
			if (fileObject instanceof File) {
				File f = (File) fileObject;
				JsonUtils.serializeIntoJsonFile(objectToSerialize, f, true);
			}
		} catch (Exception e) {
			ExceptionsUtils.ManageException(e, logger);
		}

	}

	@Override
	public boolean isFolderExist(Object folder) {
		if (folder == null) {
			return false;
		}
		if (folder instanceof File) {
			File f = (File) folder;
			if (f.exists() && f.isDirectory()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isFile(Object o) {
		if (o != null && o instanceof File) {
			File f = (File) o;
			return f.isFile();
		}
		return false;
	}

	@Override
	public boolean isFolder(Object o) {
		if (o != null && o instanceof File) {
			File f = (File) o;
			return f.isDirectory();
		}
		return false;
	}

	@Override
	public String getName(Object o) {
		if (o != null && o instanceof File) {
			File f = (File) o;
			return f.getName();
		}
		return null;
	}

	@Override
	public boolean isFileExist(Object file) {
		if (file == null) {
			return false;
		}
		if (file instanceof File) {
			File f = (File) file;
			if (f.exists() && f.isFile()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object getParentFolder(Object child) {
		if (child == null) {
			return null;
		}
		if (child instanceof File) {
			File f = (File) child;
			return f.getParentFile();
		}
		return null;
	}

	@Override
	public Object getFile(Object parentFolder, String fileName) {
		if (parentFolder == null) {
			return null;
		}
		if (parentFolder instanceof File) {
			File f = (File) parentFolder;
			f = new File(f, fileName);
			return f;
		}
		return null;
	}

	@Override
	public Object getFolder(Object parentFolder, String name) {
		if (parentFolder == null) {
			return null;
		}
		if (parentFolder instanceof File) {
			File f = (File) parentFolder;
			f = new File(f, name);
			return f;
		}
		return null;
	}

	@Override
	public List<Object> getChildFolders(Object folder) {
		if (folder == null) {
			return null;
		}
		if (folder instanceof File && isFolderExist(folder)) {
			File f = (File) folder;
			File[] ar = f.listFiles(new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					return pathname != null && pathname.exists() && pathname.isDirectory();
				}
			});
			ArrayList<Object> arl = new ArrayList<>();
			Collections.addAll(arl, ar);
			return arl;
		}
		return null;
	}

	@Override
	public void deleteFolder(Object folderToTest) {
		try {
			if (folderToTest != null && folderToTest instanceof File) {
				FileUtils.forceDelete((File) folderToTest);
			}
		} catch (Exception e) {
			ExceptionsUtils.ManageException(e, logger);
		}

	}

	@Override
	public void deleteFile(Object folderToTest) {
		try {
			if (folderToTest != null && folderToTest instanceof File) {
				FileUtils.forceDelete((File) folderToTest);
			}
		} catch (Exception e) {
			ExceptionsUtils.ManageException(e, logger);
		}
	}

	@Override
	public Object createFolder(Object parentFolder) {
		return createFolder(parentFolder, null);
	}

	@Override
	public Object createFolder(Object parentFolder, String folderName) {
		try {
			if (parentFolder == null) {
				return null;
			}
			if (parentFolder instanceof File) {
				File f = (File) parentFolder;
				if (isNotNullOrEmpty(folderName)) {
					f = new File(f, folderName);
				}
				FileUtils.forceMkdir(f);
				if (!f.exists() || !f.isDirectory()) {
					ExceptionsUtils.throwDynamicExceptionInvalidInput();
				}
				return f;
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
			if (folder1 instanceof File && folder2 instanceof File) {
				File f1 = (File) folder1;
				File f2 = (File) folder2;
				return f1.getAbsolutePath().equals(f2.getAbsolutePath());
			}
		}
		return false;
	}

	@Override
	public String getUniqueIdForFolderOrFile(Object o) {
		if (o instanceof File) {
			File f = (File) o;
			return f.getAbsolutePath();
		}
		return null;
	}

	@Override
	public boolean isFileSystemElement(Object o) {
		return o instanceof File;
	}

}

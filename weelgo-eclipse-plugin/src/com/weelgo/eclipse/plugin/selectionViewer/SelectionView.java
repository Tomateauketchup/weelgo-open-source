package com.weelgo.eclipse.plugin.selectionViewer;

import java.util.List;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;

import com.weelgo.core.CoreUtils;
import com.weelgo.eclipse.plugin.KeyHelper;
import com.weelgo.eclipse.plugin.job.CMJob;

public abstract class SelectionView<T> implements ISelectionView<T> {

	private SelectionViewerPart part;

	@Override
	public void disposeObject() {
		part=null;	
	}
	
	public SelectionViewerPart getPart() {
		return part;
	}

	public void setPart(SelectionViewerPart part) {
		this.part = part;
	}

	public void updateStatus(String message) {
		if (part != null) {
			part.updateStatus(message);
		}
	}

	public Object getCurrentData() {
		return part.getCurrentData();
	}

	public void doChanges(List<CMJob> jobs) {
		part.doChanges(jobs);
	}

	public void applyChangedDone() {
		part.applyChangedDone();
	}

	public String cleanString(String str) {
		return CoreUtils.cleanString(str);
	}

	public boolean isStrictlyEqualsString(String o1, String o2) {

		return CoreUtils.isStrictlyEqualsString(o1, o2);

	}

	public static boolean isNotNullOrEmpty(String str) {
		return CoreUtils.isNotNullOrEmpty(str);
	}

	public void addValidateWithEnter(Text t) {
		if (t != null) {
			t.addKeyListener(new KeyListener() {

				@Override
				public void keyReleased(KeyEvent e) {
					if (KeyHelper.isENTER(e)) {
						part.applyChanges();
					}
				}

				@Override
				public void keyPressed(KeyEvent e) {

				}
			});
		}
	}
}

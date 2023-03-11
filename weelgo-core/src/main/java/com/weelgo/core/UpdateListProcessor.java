package com.weelgo.core;

import java.util.ArrayList;
import java.util.List;

public abstract class UpdateListProcessor<T> {
	private List<T> newElements = new ArrayList<>();
	private List<T> toRemoveElements = new ArrayList<>();
	private List<T> notChangedElements = new ArrayList<>();
	private List<T> changedElements = new ArrayList<>();

	public abstract String getUuid(T o);

	public abstract void setUuid(T o, String uuid);

	public abstract boolean isEqual(T oldElem, T newElem);

	public abstract String generateUUid();

	public UpdateListProcessor(List<T> oldTagsArray, List<T> newTagsArray) {
		update(oldTagsArray, newTagsArray);
	}

	public void update(List<T> oldTagsArray, List<T> newTagsArray) {
		// On détecte maintenant les tags qui ont été créés
		if (newTagsArray != null) {
			for (T t1 : newTagsArray) {
				if (t1 != null) {
					boolean isCreated = true;
					if (oldTagsArray != null) {
						for (T t2 : oldTagsArray) {
							if (t2 != null && CoreUtils.isStrictlyEqualsString(getUuid(t1), getUuid(t2))) {
								isCreated = false;
								break;
							}
						}
					}
					if (isCreated) {
						// Au passage on lui donne un uuid si il n'en a pas
						if (!CoreUtils.isNotNullOrEmpty(getUuid(t1)))
							setUuid(t1, generateUUid());
						newElements.add(t1);
					}
				}

			}
		}

		// On détecte les tgs qui dégagent, qui ont changé ou pas
		if (oldTagsArray != null) {
			for (T oldElem : oldTagsArray) {
				if (oldElem != null) {
					boolean remove = true;
					boolean hasChanged = false;
					T changedTag = null;
					if (newTagsArray != null) {
						for (T newElem : newTagsArray) {
							if (newElem != null
									&& CoreUtils.isStrictlyEqualsString(getUuid(oldElem), getUuid(newElem))) {
								remove = false;

								// On regarde si le tag a changé
								if (isEqual(oldElem, newElem)) {
									hasChanged = false;
								} else {
									hasChanged = true;
									changedTag = newElem;
								}

								break;
							}
						}
					}
					if (remove) {
						toRemoveElements.add(oldElem);
					} else {
						if (hasChanged) {
							changedElements.add(changedTag);
						} else {
							notChangedElements.add(oldElem);
						}
					}
				}
			}
		}
	}

	public List<T> getNewElements() {
		return newElements;
	}

	public void setNewElements(List<T> newElements) {
		this.newElements = newElements;
	}

	public List<T> getToRemoveElements() {
		return toRemoveElements;
	}

	public void setToRemoveElements(List<T> toRemoveElements) {
		this.toRemoveElements = toRemoveElements;
	}

	public List<T> getNotChangedElements() {
		return notChangedElements;
	}

	public void setNotChangedElements(List<T> notChangedElements) {
		this.notChangedElements = notChangedElements;
	}

	public List<T> getChangedElements() {
		return changedElements;
	}

	public List<T> getChangedAndNotElements() {
		List<T> st = new ArrayList<>();
		st.addAll(getChangedElements());
		st.addAll(getNotChangedElements());

		return st;
	}

	public void setChangedElements(List<T> changedElements) {
		this.changedElements = changedElements;
	}

	public List<T> compileList() {
		List<T> newList = new ArrayList<>();
		newList.addAll(notChangedElements);
		newList.addAll(changedElements);
		newList.addAll(newElements);
		// Attention, ne pas retourner null, merci.
		return newList;
	}

	public boolean hasChanged() {
		return !(changedElements.size() == 0 && newElements.size() == 0 && toRemoveElements.size() == 0);
	}
}
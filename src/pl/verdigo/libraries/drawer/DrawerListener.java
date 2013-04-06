package pl.verdigo.libraries.drawer;

public interface DrawerListener
{

	/**
	 * Method invoked before closing drawer.
	 */
	void onDrawerBeforeCancel();

	/**
	 * Method invoked before showing drawer.
	 */
	void onDrawerBeforeShow();

	/**
	 * Method invoked after showing animation ended.
	 */
	void onDrawerAfterShow();

	/**
	 * Method invoked after closing animation ended.
	 */
	void onDrawerAfterCancel();

}

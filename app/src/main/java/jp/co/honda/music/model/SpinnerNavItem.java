package jp.co.honda.music.model;

public class SpinnerNavItem {

	private String title;
	private int icon;
	private boolean isSelected;
	
	public SpinnerNavItem(String title, int icon, boolean isSelect){
		this.title = title;
		this.icon = icon;
		this.isSelected = isSelect;
	}
	
	public String getTitle(){
		return this.title;		
	}
	
	public int getIcon(){
		return this.icon;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean selected) {
		isSelected = selected;
	}
}

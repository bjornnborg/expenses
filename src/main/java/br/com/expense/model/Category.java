package br.com.expense.model;

public class Category {
	
	private String name;

	public Category(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean equals = false;
		if (obj != null && Category.class.equals(obj.getClass())) {
			Category other = (Category) obj;
			equals = this.getName().equals(other.getName());
		}
		return equals;
	}
	
	@Override
	public int hashCode() {
		return this.getName() != null ? this.getName().hashCode() : 17;
	}
	
}

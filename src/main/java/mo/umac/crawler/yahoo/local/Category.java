package mo.umac.crawler.yahoo.local;

public class Category {
	private int id;
	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Category [id=" + id + ", name=" + name + "]");
		return sb.toString();
	}
}

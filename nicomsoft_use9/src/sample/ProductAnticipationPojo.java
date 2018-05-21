package sample;

import java.io.Serializable;

public class ProductAnticipationPojo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;
	
	private String ship_no;
	
	private String goods_category;
	
	private String goods_pid;
	
	private String img_url;
	
	private int remark;
	
	private String createtime;
	
	private int amounts;
	
	private int conAmount;
	
	private int alikey;
	public int getAlikey() {
		return alikey;
	}

	public void setAlikey(int alikey) {
		this.alikey = alikey;
	}

	public int getAmounts() {
		return amounts;
	}

	public void setAmounts(int amounts) {
		this.amounts = amounts;
	}

	public int getConAmount() {
		return conAmount;
	}

	public void setConAmount(int conAmount) {
		this.conAmount = conAmount;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getShip_no() {
		return ship_no;
	}

	public void setShip_no(String ship_no) {
		this.ship_no = ship_no;
	}

	public String getGoods_category() {
		return goods_category;
	}

	public void setGoods_category(String goods_category) {
		this.goods_category = goods_category;
	}

	public String getGoods_pid() {
		return goods_pid;
	}

	public void setGoods_pid(String goods_pid) {
		this.goods_pid = goods_pid;
	}

	public String getImg_url() {
		return img_url;
	}

	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}

	public int getRemark() {
		return remark;
	}

	public void setRemark(int remark) {
		this.remark = remark;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

  
}

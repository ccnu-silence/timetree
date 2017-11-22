package com.yey.kindergaten.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

import java.util.ArrayList;
import java.util.List;

@Table(name="selfInfo")
public class SelfInfo extends EntityBase{
	@Column(column="relationship")
	private int relationship;
	@Column(column="uid")
	private int uid;
	@Column(column="kid")
	private int kid;
	@Column(column="groupnum")
	private String groupnum;
	@Column(column="name")
	private String name;
	@Column(column="account")
	private String account;
	@Column(column="nickname")
	private String nickname;
	@Column(column="location")
	private String location;
	@Column(column="job")
	private String job;
	@Column(column="kindergarten")
	private String kindergarten;
	@Column(column="gender")
	private String gender;
	@Column(column="kname")
	private String kname;
	@Column(column="address")
	private String address;
	@Column(column="telephone")
	private String telephone;
	@Column(column="desc")
	private String desc;
	@Column(column="role")
	private int role;
	@Column(column="avatar")
	String avatar;
	@Column(column="domain")
	String domain;
	@Column(column="realname")
	String realname;
    @Column(column ="defaultrelation")
    int defaultrelation;

    public int getDefault_relation() {
        return defaultrelation;
    }

    public void setDefault_relation(int default_relation) {
        this.defaultrelation = default_relation;
    }

    public List<RelationShip>relationships;

    public List<RelationShip> getRelationships() {
        return relationships;
    }

    public void setRelationships(List<RelationShip> relationships) {
        this.relationships = relationships;
    }
    public   class RelationShip{

        public int getRelationship() {
            return relationship;
        }

        public void setRelationship(int relationship) {
            this.relationship = relationship;
        }

        private int relationship;

        private int hxregtag;

        public int getHxregtag() {
            return hxregtag;
        }

        public void setHxregtag(int hxregtag) {
            this.hxregtag = hxregtag;
        }
    }


    public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	String phonecode;
	
	public String getPhonecode() {
		return phonecode;
	}
	public void setPhonecode(String phonecode) {
		this.phonecode = phonecode;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public int getRelationship() {
		return relationship;
	}
	public void setRelationship(int relationship) {
		this.relationship = relationship;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getJob() {
		return job;
	}
	public void setJob(String job) {
		this.job = job;
	}
	public String getKindergarten() {
		return kindergarten;
	}
	public void setKindergarten(String kindergarten) {
		this.kindergarten = kindergarten;
	}
	public int getKid() {
		return kid;
	}
	public void setKid(int kid) {
		this.kid = kid;
	}
	
	
	public String getGroupnum() {
		return groupnum;
	}
	public void setGroupnum(String groupnum) {
		this.groupnum = groupnum;
	}
	public String getKname() {
		return kname;
	}
	public void setKname(String kname) {
		this.kname = kname;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getRealname() {
		return realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getRole() {
		return role;
	}
	public void setRole(int role) {
		this.role = role;
	}
	
	
}

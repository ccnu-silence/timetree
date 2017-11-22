package com.yey.kindergaten.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zy
 * Date: 13-12-4
 * Time: 上午11:35
 * To change this template use File | Settings | File Templates.
 * 注：过渡使用的类，目前已不适用，请勿删除，以免版本不兼容
 */
@Table(name="accountBean")
public class AccountBean extends EntityBase implements Serializable {
    private static final long serialVersionUID = -6919461967497580385L;
    @Column(column="uid")
    private int uid; //注册成功返回的uid
    @Column(column="phone")
    private String phone; //绑定的手机号，无绑定时为空字符
    @Column(column = "classalbumgw")
    private String classalbumgw;
    @Column(column = "sysop")
    private String sysop;
    @Column(column="sysgw")
    private String sysgw;//系统与业务无关的网关
    @Column(column="location")
    private String location;
    @Column(column="kphone")
    private String kphone; //"招生电话",
    @Column(column="knum")
    private String knum; //"群号",
    @Column(column="msggw")
    private String msggw; //消息与公众号网关
    @Column(column="password")
    private String password; //注册成功返回的账号
    @Column(column="kid")    //
    private int kid;
    @Column(column="kdesc")
    private String kdesc; //"简介",
    @Column(column="token")
    private String token;
    @Column(column="taskgw")
    private String taskgw; //任务与积分网关
    @Column(column="phonecode")
    private String phonecode; //验证码
    @Column(column="microweb")
    private String microweb;
    @Column(column="masterletterurl") //园长信箱
    private String masterletterurl;
    @Column(column="role")   //角色
    private int role;
    @Column(column="gender")
    private String gender; //性别
    @Column(column="question")
    private String question; //绑定的邮箱对应的密保问题,
    @Column(column = "maingw")
    private String maingw;
    @Column(column="classnotifyurl")
    private String classnotifyurl;//班级公告url
    @Column(column="notifygw")
    private String notifygw; //通知网关
    @Column(column="contactgw")
    private String contactgw;//通讯录网关
    @Column(column="schedulegw")
    private String schedulegw;//日程网关
    @Column(column="nickname")  //昵称
    private String nickname;

    @Column(column = "lifephotogw")
    private String lifephotogw;
    @Column(column="answer")
    private String answer; //密保答案,
    @Column(column="groupgw")
    private String groupgw;//群动态相关
    @Column(column = "diarygw")
    private String diarygw;
    @Column(column="avatar")
    private String avatar; //头像
    @Column(column="uploadurl")
    private String uploadurl; //文件上传url
    /**
     *relationship存在本地
     */
    @Column(column = "relationship")
    private int relationship;
    @Column(column="classscheduleurl")
    private String classscheduleurl; //教学安排url
    @Column(column="num")
    private int num;
    @Column(column="klocation")
    private String klocation;
    @Column(column="email")
    private String email; //"绑定的邮箱对应的密保问题",
    @Column(column="classphotourl")
    private String classphotourl; //班级相册url
    @Column(column="account")
    private String account; //注册成功返回的账号
    @Column(column="realname")
    private String realname;
    @Column(column="noticeurl")
    private String noticeurl; //发通知url
    @Column(column="telephone")
    private String telephone;
    @Column(column="kname")
    private String kname; //"幼儿园名字",
    @Column(column="kaddress")
    private String kaddress; //"具体地址",
    @Column(column = "rights")
    private String rights;
    @Column(column = "cname")
    private String cname; //"班级"
    @Column(column = "birthday")
    private String birthday; //"生日"
    @Column(column = "job")
    private String job;
    private int cid;

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
    public AccountBean() {
    }

    public AccountBean(AccountInfo bean) {
        this.uid = bean.getUid();
        this.phone =bean.getPhone();
        this.classalbumgw = bean.getClassalbumgw();
        this.sysgw = bean.getSysgw();
        this.sysop = bean.getSysop();
        this.location = bean.getLocation();
        this.kphone = bean.getKphone();
        this.knum = bean.getKnum();
        this.msggw = bean.getMsggw();
        this.password = bean.getPassword();
        this.kid = bean.getKid();
        this.kdesc = bean.getKdesc();
        this.token = bean.getToken();
        this.taskgw = bean.getTaskgw();
        this.phonecode = bean.getPhonecode();
        this.microweb = bean.getMicroweb();
        this.masterletterurl = bean.getMasterletterurl();
        this.role = bean.getRole();
        this.gender = bean.getGender();
        this.question = bean.getQuestion();
        this.maingw = bean.getMaingw();
        this.classnotifyurl = bean.getClassnotifyurl();
        this.notifygw = bean.getNotifygw();
        this.contactgw = bean.getContactgw();
        this.schedulegw = bean.getSchedulegw();
        this.nickname = bean.getNickname();
        this.lifephotogw = bean.getLifephotogw();
        this.answer = bean.getAnswer();
        this.groupgw = bean.getGroupgw();
        this.diarygw = bean.getDiarygw();
        this.avatar = bean.getAvatar();
        this.uploadurl = bean.getUploadurl();
        this.relationship = bean.getRelationship();
        this.classscheduleurl = bean.getClassscheduleurl();
        this.num = bean.getNum();
        this.klocation = bean.getKlocation();
        this.email = bean.getEmail();
        this.classphotourl = bean.getClassphotourl();
        this.account = bean.getAccount();
        this.realname = bean.getRealname();
        this.noticeurl = bean.getNoticeurl();
        this.telephone = bean.getAccount();
        this.kname = bean.getKname();
        this.kaddress = bean.getKaddress();
        this.defaultrelation = bean.getDefaultrelation();
        this.rights = bean.getRights();
        this.cname = bean.getCname();
        this.birthday = bean.getBirthday();
        this.job = bean.getJob();
//        this.relationships = bean.getRelationships();
    }

    /**
     * defaultrelation和relationships存在relationship表中
     */
    private int defaultrelation;
    public int getDefaultrelation() {
        return defaultrelation;
    }

    public void setDefaultrelation(int defaultrelation) {
        this.defaultrelation = defaultrelation;
    }

    public List<RelationShip> relationships;

    public List<RelationShip> getRelationships() {
        return relationships;
    }

    public void setRelationships(List<RelationShip> relationships) {
        this.relationships = relationships;
    }
    public   class RelationShip  implements Serializable{

        private int relationship;

        private int hxregtag;

        public int getHxregtag() {
            return hxregtag;
        }

        public void setHxregtag(int hxregtag) {
            this.hxregtag = hxregtag;
        }

        public int getRelationship() {
            return relationship;
        }

        public void setRelationship(int relationship) {
            this.relationship = relationship;
        }


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
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public int getRole() {
        return role;
    }
    public void setRole(int role) {
        this.role = role;
    }
    public int getKid() {
        return kid;
    }
    public void setKid(int kid) {
        this.kid = kid;
    }
    public int getNum() {
        return num;
    }
    public void setNum(int num) {
        this.num = num;
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
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getPhonecode() {
        return phonecode;
    }
    public void setPhonecode(String phonecode) {
        this.phonecode = phonecode;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getKlocation() {
        return klocation;
    }
    public void setKlocation(String klocation) {
        this.klocation = klocation;
    }
    public String getKname() {
        return kname;
    }
    public void setKname(String kname) {
        this.kname = kname;
    }
    public String getKnum() {
        return knum;
    }
    public void setKnum(String knum) {
        this.knum = knum;
    }
    public String getKaddress() {
        return kaddress;
    }
    public void setKaddress(String kaddress) {
        this.kaddress = kaddress;
    }
    public String getKphone() {
        return kphone;
    }
    public void setKphone(String kphone) {
        this.kphone = kphone;
    }
    public String getKdesc() {
        return kdesc;
    }
    public void setKdesc(String kdesc) {
        this.kdesc = kdesc;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
    public String getAnswer() {
        return answer;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getMicroweb() {
        return microweb;
    }
    public void setMicroweb(String microweb) {
        this.microweb = microweb;
    }
    public String getContactgw() {
        return contactgw;
    }
    public void setContactgw(String contactgw) {
        this.contactgw = contactgw;
    }
    public String getMsggw() {
        return msggw;
    }
    public void setMsggw(String msggw) {
        this.msggw = msggw;
    }
    public String getTaskgw() {
        return taskgw;
    }
    public void setTaskgw(String taskgw) {
        this.taskgw = taskgw;
    }
    public String getSysgw() {
        return sysgw;
    }
    public void setSysgw(String sysgw) {
        this.sysgw = sysgw;
    }
    public String getGroupgw() {
        return groupgw;
    }
    public void setGroupgw(String groupgw) {
        this.groupgw = groupgw;
    }
    public String getSchedulegw() {
        return schedulegw;
    }
    public void setSchedulegw(String schedulegw) {
        this.schedulegw = schedulegw;
    }
    public String getNotifygw() {
        return notifygw;
    }
    public void setNotifygw(String notifygw) {
        this.notifygw = notifygw;
    }
    public String getClassnotifyurl() {
        return classnotifyurl;
    }
    public void setClassnotifyurl(String classnotifyurl) {
        this.classnotifyurl = classnotifyurl;
    }
    public String getClassphotourl() {
        return classphotourl;
    }
    public void setClassphotourl(String classphotourl) {
        this.classphotourl = classphotourl;
    }
    public String getClassscheduleurl() {
        return classscheduleurl;
    }
    public void setClassscheduleurl(String classscheduleurl) {
        this.classscheduleurl = classscheduleurl;
    }
    public String getMasterletterurl() {
        return masterletterurl;
    }
    public void setMasterletterurl(String masterletterurl) {
        this.masterletterurl = masterletterurl;
    }
    public String getNoticeurl() {
        return noticeurl;
    }
    public void setNoticeurl(String noticeurl) {
        this.noticeurl = noticeurl;
    }
    public String getUploadurl() {
        return uploadurl;
    }
    public void setUploadurl(String uploadurl) {
        this.uploadurl = uploadurl;
    }

    public String getMaingw() {
        return maingw;
    }

    public void setMaingw(String maingw) {
        this.maingw = maingw;
    }

    public String getSysop() {
        return sysop;
    }

    public void setSysop(String sysop) {
        this.sysop = sysop;
    }

    public String getDiarygw() {
        return diarygw;
    }

    public void setDiarygw(String diarygw) {
        this.diarygw = diarygw;
    }

    public String getClassalbumgw() {
        return classalbumgw;
    }

    public void setClassalbumgw(String classalbumgw) {
        this.classalbumgw = classalbumgw;
    }

    public String getLifephotogw() {
        return lifephotogw;
    }

    public void setLifephotogw(String lifephotogw) {
        this.lifephotogw = lifephotogw;
    }

    public int getRelationship() {
        return relationship;
    }

    public void setRelationship(int relationship) {
        this.relationship = relationship;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}

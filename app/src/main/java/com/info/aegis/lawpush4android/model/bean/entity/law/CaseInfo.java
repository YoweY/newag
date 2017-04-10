package com.info.aegis.lawpush4android.model.bean.entity.law;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 案件信息
 *
 * @author wuhao
 */
public class CaseInfo implements Cloneable {


	public static final String CRIME_TIME                        = "crimeTime";
	public static final String LAW_IDS                           = "lawIds";
	public static final String NESTED_AGENT_REFLECTS             = "agentReflects";
	public static final String CASE_RESULT                       = "caseResult";
	public static final String PARTIES_WORK_UNIT_TYPE            = "parties.workUnitType";
	public static final String PARAS_TAGS                        = "paras.tags";
	public static final String JUDGE_ID                          = "judgeId";
	public static final String PARAS_SENTENCES_WORDS_NESTED_ARGS = "paras.sentences.words.args";
	public static final String PARAS_SENTENCES_WORDS_POS         = "paras.sentences.words.pos";
	public static final String NESTED_SENTENCES                  = "sentences";
	public static final String LABEL_REFLECTS_VALUE              = "labelReflects.value";
	public static final String PARAS_CONTENT                     = "paras.content";
	public static final String CASE_CAUSE_ID                     = "caseCauseId";
	public static final String PARTIES_ID                        = "parties.id";
	public static final String TOP_CAUSE_ID                      = "topCauseId";
	public static final String PARAS_SENTENCES_NESTED_WORDS      = "paras.sentences.words";
	public static final String CONTENT                           = "content";
	public static final String PARTIES_PRE_CAUSE_ID              = "parties.preCauseId";
	public static final String PLOTS_LABEL_ID                    = "plots.labelId";
	public static final String SUBJECT_DESP                      = "subjectDesp";
	public static final String PARAS_ID                          = "paras.id";
	public static final String TRIAL_TIME                        = "trialTime";
	public static final String PLOTS_ID                          = "plots.id";
	public static final String PARAS_SENTENCES_WORDS_ARGS_BEGIN  = "paras.sentences.words.args.begin";
	public static final String PARTIES_NATURAL_PERSON_TYPE       = "parties.naturalPersonType";
	public static final String PARAS_SENTENCES_WORDS_TAGS        = "paras.sentences.words.tags";
	public static final String NESTED_PARTIES                    = "parties";
	public static final String AGENT_IDS                         = "agentIds";
	public static final String PARTIES_TYPE                      = "parties.type";
	public static final String NESTED_PARAS                      = "paras";
	public static final String GRADE                             = "grade";
	public static final String PARAS_SENTENCES_WORDS_PARENT_ID   = "paras.sentences.words.parentId";
	public static final String PARAS_SENTENCES_WORDS_CONTENT     = "paras.sentences.words.content";
	public static final String PARTIES_WIN_SUIT                  = "parties.winSuit";
	public static final String DESCRIPTION                       = "description";
	public static final String PLOTS_NUM_ATTR                    = "plots.numAttr";
	public static final String CITY_PROVINCE_ID                  = "city.province.id";
	public static final String PARAS_SENTENCES_WORDS_END         = "paras.sentences.words.end";
	public static final String NESTED_LABEL_REFLECTS             = "labelReflects";
	public static final String PLOTS_ATTR                        = "plots.attr";
	public static final String ID                                = "id";
	public static final String AGENT_REFLECTS_PARTY_TYPE         = "agentReflects.partyType";
	public static final String PARAS_SENTENCES_WORDS_ID          = "paras.sentences.words.id";
	public static final String PARAS_SENTENCES_TAGS              = "paras.sentences.tags";
	public static final String PARTIES_EDU_DEGREE                = "parties.eduDegree";
	public static final String DECIDE_TIME                       = "decideTime";
	public static final String PARTIES_DEGREE                    = "parties.degree";
	public static final String SUBJECT_AMOUNT_GRADE              = "subjectAmountGrade";
	public static final String SOURCE_TYPE                       = "sourceType";
	public static final String PROVINCE_ID                       = "province.id";
	public static final String LABEL_REFLECTS_ID                 = "labelReflects.id";
	public static final String PARAS_SENTENCES_WORDS_NE          = "paras.sentences.words.ne";
	public static final String PARTIES_CAUSE_NAME                = "parties.causeName";
	public static final String PARTIES_IDENTITY                  = "parties.identity";
	public static final String NESTED_PLOTS                      = "plots";
	public static final String PARAS_NESTED_SENTENCES            = "paras.sentences";
	public static final String PARAS_SENTENCES_WORDS_RELATION    = "paras.sentences.words.relation";
	public static final String PARTIES_SEX                       = "parties.sex";
	public static final String PARAS_SENTENCES_WORDS_ARGS_ID     = "paras.sentences.words.args.id";
	public static final String PARTIES_IS_CRIMINAL_RECORD        = "parties.isCriminalRecord";
	public static final String AGENT_REFLECTS_AGENT_ID           = "agentReflects.agentId";
	public static final String PARAS_SENTENCES_WORDS_ARGS_TYPE   = "paras.sentences.words.args.type";
	public static final String PARTIES_AGE                       = "parties.age";
	public static final String AGENT_REFLECTS_WORK_UNIT          = "agentReflects.workUnit";
	public static final String PARAS_SENTENCES_ID                = "paras.sentences.id";
	public static final String AGENT_REFLECTS_ID                 = "agentReflects.id";
	public static final String PLOTS_VALUE                       = "plots.value";
	public static final String PARAS_SENTENCES_WORDS_ARGS_END    = "paras.sentences.words.args.end";
	public static final String TOP_COURT_ID                      = "topCourtId";
	public static final String LABEL_REFLECTS_LABEL_ID           = "labelReflects.labelId";
	public static final String DOC_TYPE                          = "docType";
	public static final String AGENT_REFLECTS_NAME               = "agentReflects.name";
	public static final String AGENT_REFLECTS_WIN_SUIT           = "agentReflects.winSuit";
	public static final String PARAS_SENTENCES_CONTENT           = "paras.sentences.content";
	public static final String CASE_NUMBER                       = "caseNumber";
	public static final String COURT_ID                          = "courtId";
	public static final String PARTIES_INDUSTRY_TYPE             = "parties.industryType";
	public static final String REASON_LENGTH                     = "reasonLength";
	public static final String LAST_CALC_KEY_INFO_TIME           = "lastCalcKeyInfoTime";
	public static final String SUBJECT_AMOUNT                    = "subjectAmount";
	public static final String PARTIES_LEGAL_STATUS              = "parties.legalStatus";
	public static final String NAME                              = "name";
	public static final String TOP_ORGAN_ID                      = "topOrganId";
	public static final String PARTIES_LEGAL_REPRESENTATIVE      = "parties.legalRepresentative";
	public static final String ORGAN_ID                          = "organId";
	private Integer                   caseCauseId;//案由id
	private String                    caseNumber;
	private Integer                   caseResult;
	private String                    content;
	private Date                      createTime;
	private Date                      crimeTime;//案发时间
	private Date                      decideTime;//判决时间
	private String                    description;//案件描述
	private Integer                   docType;
	/**
	 * 1:一审、2:二审、3：再审、0：其他
	 */
	private Integer                   grade;
	private String                    id;                            // 案件标识
	private Date                      lastCalcKeyInfoTime;
	private Set<String>               lawIds;
	private String                    name;//案件名称
	private Integer                   organId;
	/**
	 * 说理长度
	 */
	private Integer                   reasonLength;
	/**
	 * 来源方式，openlaw，内网数据
	 */
	private Integer                   sourceType;
	private Double                    subjectAmount;                // 标的金额;
	private Integer                   subjectAmountGrade;
	private String                    subjectDesp;                    // 标的物描述
	private Set<String>               tags;
	private Integer                   topCauseId;//顶级案由id
	private String                    topCourtId;
	private Integer                   topOrganId;//所属高院的机构id
	private Integer                   trialTime;//审理时长(单位：天)
	private Integer                   docLength;//文书长度
	private String 					  closedWay;//结案方式
	private String 					  undertakerId; //承办人id
	private String 					  clerkId; //书记员id
//	@Field(type = FieldType.Integer)
//	private Integer 				  courtId;// 法院id

	private String 					   title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public CaseInfo() {

	}

	public String getClerkId() {
		return clerkId;
	}

	public void setClerkId(String clerkId) {
		this.clerkId = clerkId;
	}

	public String getUndertakerId() {
		return undertakerId;
	}

	public void setUndertakerId(String undertakerId) {
		this.undertakerId = undertakerId;
	}

	public String getClosedWay() {
		return closedWay;
	}

	public void setClosedWay(String closedWay) {
		this.closedWay = closedWay;
	}

	public Integer getDocLength() {
		return docLength;
	}

	public void setDocLength(Integer docLength) {
		this.docLength = docLength;
	}


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Set<String> getLawIds() {
		return lawIds;
	}

	public void setLawIds(Set<String> lawIds) {
		this.lawIds = lawIds;
	}

	public Integer getDocType() {
		return docType;
	}

	public void setDocType(Integer docType) {
		this.docType = docType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getCaseCauseId() {
		return caseCauseId;
	}

	public void setCaseCauseId(Integer caseCauseId) {
		this.caseCauseId = caseCauseId;
	}


	public Date getDecideTime() {
		return decideTime;
	}

	public void setDecideTime(Date decideTime) {
		this.decideTime = decideTime;
	}

	public Integer getOrganId() {
		return organId;
	}

	public void setOrganId(Integer organId) {
		this.organId = organId;
	}

	public Date getLastCalcKeyInfoTime() {
		return lastCalcKeyInfoTime;
	}

	public void setLastCalcKeyInfoTime(Date lastCalcKeyInfoTime) {
		this.lastCalcKeyInfoTime = lastCalcKeyInfoTime;
	}

	public Integer getTopCauseId() {
		return topCauseId;
	}

	public void setTopCauseId(Integer topCauseId) {
		this.topCauseId = topCauseId;
	}

	public Integer getSourceType() {
		return sourceType;
	}

	public void setSourceType(Integer sourceType) {
		this.sourceType = sourceType;
	}

	public Double getSubjectAmount() {
		return subjectAmount;
	}

	public void setSubjectAmount(Double subjectAmount) {
		this.subjectAmount = subjectAmount;
	}

	public String getSubjectDesp() {
		return subjectDesp;
	}

	public void setSubjectDesp(String subjectDesp) {
		this.subjectDesp = subjectDesp;
	}

	public int getSubjectAmountGrade() {
		return subjectAmountGrade;
	}

	public void setSubjectAmountGrade(int subjectAmountGrade) {
		this.subjectAmountGrade = subjectAmountGrade;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public Integer getReasonLength() {
		return reasonLength;
	}

	public void setReasonLength(Integer reasonLength) {
		this.reasonLength = reasonLength;
	}

	public int getTrialTime() {
		return trialTime;
	}

	public void setTrialTime(int trialTime) {
		this.trialTime = trialTime;
	}

	public Integer getCaseResult() {
		return caseResult;
	}

	public void setCaseResult(Integer caseResult) {
		this.caseResult = caseResult;
	}

	public String getCaseNumber() {
		return caseNumber;
	}

	public void setCaseNumber(String caseNumber) {
		this.caseNumber = caseNumber;
	}

	public String getTopCourtId() {
		return topCourtId;
	}

	public void setTopCourtId(String topCourtId) {
		this.topCourtId = topCourtId;
	}

	public Integer getTopOrganId() {
		return topOrganId;
	}

	public void setTopOrganId(Integer topOrganId) {
		this.topOrganId = topOrganId;
	}

	public void addLawId(String lawId) {
		if (this.lawIds == null) {
			this.lawIds = new HashSet<>();
		}
		this.lawIds.add(lawId);
	}

	public Date getCrimeTime() {
		return crimeTime;
	}

	public void setCrimeTime(Date crimeTime) {
		this.crimeTime = crimeTime;
	}

	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}
//
//	public Integer getCourtId() {
//		return courtId;
//	}
//
//	public void setCourtId(Integer courtId) {
//		this.courtId = courtId;
//	}

//	public String getCourtName(int courtId){
//		return "法院";
//	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

}

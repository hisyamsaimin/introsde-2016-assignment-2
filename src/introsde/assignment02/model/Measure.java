package introsde.assignment02.model;

import introsde.assignment02.dao.Assignment02Dao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * The Measure class represents recorded health measures of a Person. It has a 
 * measureId attribute used to identify every Measure in the database. Every Measure 
 * belongs to a Person and the person attribute maps each Measure to a person in a 
 * ManyToOne relationship. A Measure has a MeasureType to describe the kind of health 
 * measure that is being recorded and the measureType attribute maps each Measure to
 * a MeasureType in a ManyToOne relationship. The value attribute is used to record
 * the value the Measure.
 * 
 *
 *
 */
@Entity 
@Table(name="Measure")
@NamedQueries({
  @NamedQuery(
    name = "Measure.findMeasuresFromPerson",
    query = "SELECT m FROM Measure m, MeasureType mt WHERE m.person.personId = :pid AND m.measureType.measureTypeId = mt.measureTypeId AND mt.name = :measureName ORDER BY m.measureId DESC"
  ),
  @NamedQuery(
    name = "Measure.findCurrentMeasuresFromPerson",
    query = "SELECT m FROM Measure m, MeasureType mt WHERE m.person.personId = :pid AND m.measureType.measureTypeId = mt.measureTypeId GROUP BY mt.measureTypeId"
  )
})
@XmlRootElement
@XmlType(propOrder={"measureId","value","measureType","created"})
public class Measure implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(generator="sqlite_measure")
  @TableGenerator(name="sqlite_measure", table="sqlite_sequence",
    pkColumnName="name", valueColumnName="seq",
    pkColumnValue="Measure")
  @Column(name="measureId")
  private int measureId;
  @Column(name="value")
  private String value;
  @Temporal(TemporalType.DATE)
  @Column(name="created")
  private Date created;

  @ManyToOne
  @JoinColumn(name="measureTypeId",referencedColumnName="measureTypeId")
  private MeasureType measureType;

  @ManyToOne
  @JoinColumn(name="personId", referencedColumnName="personId")
  private Person person;
  
  // Getters
  @XmlElement(name="mid")
  public int getMeasureId(){
    return measureId;
  }
  public String getValue(){
    return value;
  }
  @XmlTransient
  public Person getPerson(){
    return person;
  }
  @XmlElement(name="measureName")
  public MeasureType getMeasureType(){
    return measureType;
  }
  public Date getCreated(){
    return created;
  }
  
  // Setters
  public void setMeasureId(int measureId){
    this.measureId = measureId;
  }
  public void setValue(String value){
    this.value = value;
  }
  public void setPerson(Person person){
    this.person = person;
  }
  public void setMeasureType(MeasureType measureType){
    if( measureType.getMeasureTypeId() == 0 ) // New measure with just the measure type name set
      measureType = MeasureType.findFromName(measureType.getName()); // Set the measureType from the name
    
    this.measureType = measureType;
  }
  public void setCreated(Date created){
    this.created = created;
  }
  
  /**
   * Method used when creating a new measure and only the name of the MeasureType is available.
   * This method finds a MeasureType using its name and sets it to the Measure.
   * 
   * @param measureName  The value of the name of the MeasureType we want to associate to the current Measure object.
   */
  public void setMeasureTypeFromMeasureName(String measureName){
    MeasureType measureType = MeasureType.findFromName(measureName);
    setMeasureType(measureType);
  }

  /**
   * Finds a measure given its id.
   * 
   * @param measureId	  The id that identifies a measure in the database.
   * @return			      The found Measure object.
   */
  public static Measure getMeasureById(int measureId) {
    EntityManager em = Assignment02Dao.instance.createEntityManager();
    Measure measure = em.find(Measure.class, measureId);
    Assignment02Dao.instance.closeConnections(em);
    return measure;
  }
  
  /**
   * Gets the measures for a given Person and a given MeasureType name.
   * 
   * @param personId		  The id of the person.
   * @param measureName		The name of the MeasureType.
   * @return				      A list of a Person's Measures of a given MeasureType
   */
  public static List<Measure> getMeasuresFromPerson(int personId, String measureName) {
    EntityManager em = Assignment02Dao.instance.createEntityManager();
    List<Measure> list = em.createNamedQuery("Measure.findMeasuresFromPerson", Measure.class)
      .setParameter("pid", personId)
      .setParameter("measureName", measureName)
      .getResultList();
    Assignment02Dao.instance.closeConnections(em);
    return list;
  }

  /**
   * Gets the last recorded measure of every MeasureType for a given Person.
   * 
   * @param personId	 The id of the person.
   * @return			     A list of the current Measures of a Person
   */
  public static List<Measure> getCurrentMeasuresFromPerson(int personId) {
    EntityManager em = Assignment02Dao.instance.createEntityManager();
    List<Measure> list = em.createNamedQuery("Measure.findCurrentMeasuresFromPerson", Measure.class)
      .setParameter("pid", personId)
      .getResultList();
    Assignment02Dao.instance.closeConnections(em);
    return list;
  }

  /**
   * Creates a Measure in the database.
   * 
   * @param measure			  The Measure to be persisted in the database.
   * @param personId		  The id of the person the Measure belongs to.
   * @param measureName		The name of the MeasureType of the Measure.
   * @return				      The created Measure.
   */
  public static Measure createMeasure(Measure measure, int personId, String measureName) {
    measure.setPerson(Person.getPersonById(personId));
    measure.setMeasureTypeFromMeasureName(measureName);
    measure.setCreated(new Date()); // The created date is always the date the measure is being created on

    EntityManager em = Assignment02Dao.instance.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.begin();
    em.persist(measure);
    tx.commit();
    Assignment02Dao.instance.closeConnections(em);
    return measure;
  }

  /**
   * Updates a Measure in the database. It makes sure to NOT override any data that 
   * is not being updated.
   * 
   * @param oldMeasure		  The measure with the currently saved data.
   * @param updatedMeasure	The measure with the updated data that will be saved.
   * @return				        The updated measure.
   */
  public static Measure updateMeasure(Measure oldMeasure, Measure updatedMeasure) {
    //When updating a measure only the value attribute can be changed
    updatedMeasure.setMeasureId(oldMeasure.getMeasureId());
    updatedMeasure.setPerson(oldMeasure.getPerson());
    updatedMeasure.setMeasureType(oldMeasure.getMeasureType());

    EntityManager em = Assignment02Dao.instance.createEntityManager(); 
    EntityTransaction tx = em.getTransaction();
    tx.begin();
    updatedMeasure = em.merge(updatedMeasure);
    tx.commit();
    Assignment02Dao.instance.closeConnections(em);
    return updatedMeasure;
  }
}
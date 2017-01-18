package introsde.assignment02.model;

import introsde.assignment02.dao.Assignment02Dao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlTransient;

/**
 * The Person class represent a person that will record health measures. It has a 
 * personId attribute used to identify every Person in the database.The firstname, 
 * lastname and birthdate attributes represent personal data of the Person. The measures
 * attribute represents the measures the person has saved, it is mapped by a OneToMany
 * relationship.
 * 
 *
 *
 */
@Entity
@Table(name="Person") 
@NamedQuery(name="Person.findAll", query="SELECT p FROM Person p")
@XmlRootElement
@XmlType(propOrder={"personId","firstname","lastname", "birthdate", "currentMeasures"})
public class Person implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(generator="sqlite_person")
  @TableGenerator(name="sqlite_person", table="sqlite_sequence",
    pkColumnName="name", valueColumnName="seq",
    pkColumnValue="Person")
  @Column(name="personId")
  private int personId;
  @Column(name="lastname")
  private String lastname;
  @Column(name="firstname")
  private String firstname;
  @Temporal(TemporalType.DATE)
  @Column(name="birthdate")
  private Date birthdate;

  @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
  @JoinColumn(name="personId", referencedColumnName="personId")
  private List<Measure> measures;
  
  /**
   * This attribute is not persisted in the database, it's just used to represent the
   * latest (current) measures that a person has recorded. It is used to display
   * the Health Profile.
   */
  @Transient
  private List<Measure> currentMeasures;
  
  // Getters
  public int getPersonId(){
    return personId;
  }
  public String getLastname(){
    return lastname;
  }
  public String getFirstname(){
    return firstname;
  }
  public Date getBirthdate(){
    return birthdate;
  }
  @XmlTransient
  public List<Measure> getMeasures(){
    return measures;
  }

  @XmlElementWrapper(name="healthProfile")
  @XmlElement(name="measure")
  public List<Measure> getCurrentMeasures(){
  // When the currentMeasures is not empty it means it's reading the measures of a person that will be created with some initial measures.
    if (this.currentMeasures == null)
      this.currentMeasures = Measure.getCurrentMeasuresFromPerson(this.personId); // If the currentMeasures is empty then it loads the lates measures of a person to represent the Health Profile 

    return currentMeasures;
  }
  
  // Setters
  public void setPersonId(int personId){
    this.personId = personId;
  }
  public void setLastname(String lastname){
    this.lastname = lastname;
  }
  public void setFirstname(String firstname){
    this.firstname = firstname;
  }
  public void setBirthdate(Date birthdate){
    this.birthdate = birthdate;
  }
  public void setMeasures(List<Measure> measures){
    this.measures = measures;
  }
  public void setCurrentMeasures(List<Measure> currentMeasures){
    this.currentMeasures = currentMeasures;
  }
  
  /**
   * Returns every person in the database.
   * 
   * @return  A list of Persons
   */
  public static List<Person> getAll() {
    EntityManager em = Assignment02Dao.instance.createEntityManager();
    List<Person> list = em.createNamedQuery("Person.findAll", Person.class)
    .getResultList();
    Assignment02Dao.instance.closeConnections(em);
    return list;
  }

  /**
   * Finds a Person in the database given its id.
   * @param personId    The id of the person
   * @return            The found person
   */
  public static Person getPersonById(int personId) {
    EntityManager em = Assignment02Dao.instance.createEntityManager();
    Person person = em.find(Person.class, personId);
    if(person != null)
      em.refresh(person);
    Assignment02Dao.instance.closeConnections(em);
    return person;
  }
  
  /**
   * Creates a person in the database.
   * 
   * @param person  The person to be persisted in the database
   * @return        The saved person
   */
  public static Person createPerson(Person person) {
    EntityManager em = Assignment02Dao.instance.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.begin();
    em.persist(person);
    tx.commit();
    Assignment02Dao.instance.closeConnections(em);
    return person;
  }
  
  /**
   * This method is used by the updatePerson method to sync a person before updating
   * it on the database. When updating only specific fields, other fields will be set to
   * null, this method makes sure that the old values of the attributes that will not be 
   * updated remain in the database.
   * 
   * @param oldPerson       The person that was retrieved from the database. It contains 
   *                        the old information of a person.        
   * @param updatedPerson   The person containing only the attributes that will be updated.
   * @return                A person with updated information but also keeping its old attributes
   */
  public static Person syncPerson(Person oldPerson, Person updatedPerson) {
    updatedPerson.setPersonId(oldPerson.getPersonId());
    updatedPerson.setMeasures(oldPerson.getMeasures()); // Prevent Measures to be lost when updating a person

    if (updatedPerson.getFirstname() == null)
      updatedPerson.setFirstname(oldPerson.getFirstname());

    if (updatedPerson.getLastname() == null)
      updatedPerson.setLastname(oldPerson.getLastname());

    if (updatedPerson.getBirthdate() == null)
      updatedPerson.setBirthdate(oldPerson.getBirthdate());

    return updatedPerson;
  }

  /**
   * Updates a Person in the database. It makes sure to NOT override any data that 
   * is not being updated.
   * 
   * @param oldPerson     The person with the currently saved data.
   * @param updatedPerson The person with the updated data that will be saved.
   * @return              The updated person
   */
  public static Person updatePerson(Person oldPerson, Person updatedPerson) {
    updatedPerson = syncPerson(oldPerson, updatedPerson);

    EntityManager em = Assignment02Dao.instance.createEntityManager(); 
    EntityTransaction tx = em.getTransaction();
    tx.begin();
    updatedPerson = em.merge(updatedPerson);
    tx.commit();
    Assignment02Dao.instance.closeConnections(em);
    return updatedPerson;
  }

  /**
   * Deletes a Person from the database.
   * 
   * @param person  The person that will be deleted.
   */
  public static void deletePerson(Person person) {
    EntityManager em = Assignment02Dao.instance.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.begin();
    person = em.merge(person);
    em.remove(person);
    tx.commit();
    Assignment02Dao.instance.closeConnections(em);
  }
}
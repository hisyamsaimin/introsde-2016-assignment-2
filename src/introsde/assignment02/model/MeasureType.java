package introsde.assignment02.model;

import introsde.assignment02.dao.Assignment02Dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

/**
 * The MeasureType class represents the available types of Measures a Person 
 * can keep track of. It has a measureTypeId attribute used to identify every
 * MeasureType in the database. It also has a name attribute used to describe
 * the MeasureType.
 * 

 *
 */
@Entity
@Table(name="MeasureType") 
@NamedQueries({
  @NamedQuery(name="MeasureType.findAll", query="SELECT mt FROM MeasureType mt"),
  @NamedQuery(name="MeasureType.findFromName", query="SELECT mt FROM MeasureType mt WHERE mt.name = :name")
})
@XmlRootElement
public class MeasureType implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(generator="sqlite_measure_type")
  @TableGenerator(name="sqlite_measure_type", table="sqlite_sequence",
    pkColumnName="name", valueColumnName="seq",
    pkColumnValue="MeasureType")
  @Column(name="measureTypeId")
  private int measureTypeId;
  @Column(name="name")
  private String name;
  
  // Getters
  @XmlTransient
  public int getMeasureTypeId(){
    return measureTypeId;
  }

  @XmlValue
  public String getName(){
    return name;
  }
  
  // Setters
  public void setMeasureTypeId(int measureTypeId){
    this.measureTypeId = measureTypeId;
  }
  public void setName(String name){
    this.name = name;
  }
  
  /**
   * Returns all the available MeasureTypes
   * 
   * @return	A list of MeasureTypes.
   */
  public static List<MeasureType> getAll() {
    EntityManager em = Assignment02Dao.instance.createEntityManager();
    List<MeasureType> list = em.createNamedQuery("MeasureType.findAll", MeasureType.class).getResultList();
    Assignment02Dao.instance.closeConnections(em);
    return list;
  }

  /**
   * Finds a MeasureType in the database given its name.
   * 
   * @param name	The name of the MeasureType.
   * @return		  A MeasureType that has the given name.
   */
  public static MeasureType findFromName(String name) {
    EntityManager em = Assignment02Dao.instance.createEntityManager();
    MeasureType measureType = em.createNamedQuery("MeasureType.findFromName", MeasureType.class)
      .setParameter("name", name)
      .getSingleResult();
    Assignment02Dao.instance.closeConnections(em);
    return measureType;
  }
}
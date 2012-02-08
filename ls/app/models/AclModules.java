package models;

import play.*;
import play.db.jpa.*;
import play.data.validation.*;
import play.exceptions.JavaExecutionException;
import zwei.utils.HqlToSqlTranslator;

import javax.persistence.*;

import java.util.*;

@javax.persistence.Entity
@Table(name="acl_modules")
public class AclModules extends Model
{

    @ManyToOne
    @JoinColumn(name="parent_id")
	public AclModules parentId;

    public String title;

    public String module;

    public String tree;

    public String linkable;

    public String approved;

	/**
	 * Method 'AclModules'
	 * 
	 */
	public AclModules()
	{
	}

	/**
	 * Method 'getParentId'
	 * 
	 * @return int
	 */
	@Column(name="parent_id")
	public AclModules getParentId()
	{
		return parentId;
	}

	/**
	 * Method 'setParentId'
	 * 
	 * @param parentId
	 */
	public void setParentId(AclModules parentId)
	{
		this.parentId = parentId;
	}

	/**
	 * Method 'getTitle'
	 * 
	 * @return String
	 */
	@Column(name="title")
	public String getTitle()
	{
		return title;
	}

	/**
	 * Method 'setTitle'
	 * 
	 * @param title
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * Method 'getModule'
	 * 
	 * @return String
	 */
	@Column(name="module")
	public String getModule()
	{
		return module;
	}

	/**
	 * Method 'setModule'
	 * 
	 * @param module
	 */
	public void setModule(String module)
	{
		this.module = module;
	}

	/**
	 * Method 'getTree'
	 * 
	 * @return String
	 */
	@Column(name="tree")
	public String getTree()
	{
		return tree;
	}

	/**
	 * Method 'setTree'
	 * 
	 * @param tree
	 */
	public void setTree(String tree)
	{
		this.tree = tree;
	}

	/**
	 * Method 'getLinkable'
	 * 
	 * @return String
	 */
	@Column(name="linkable")
	public String getLinkable()
	{
		return linkable;
	}

	/**
	 * Method 'setLinkable'
	 * 
	 * @param linkable
	 */
	public void setLinkable(String linkable)
	{
		this.linkable = linkable;
	}

	/**
	 * Method 'getApproved'
	 * 
	 * @return String
	 */
	@Column(name="approved")
	public String getApproved()
	{
		return approved;
	}

	/**
	 * Method 'setApproved'
	 * 
	 * @param approved
	 */
	public void setApproved(String approved)
	{
		this.approved = approved;
	}
	
	public String toString() 
	{
		return title;
	}

	public List<AclModules> listGrantedResourcesByParentId(int parentId)
	{	
		List<AclModules> returns = null;
		Query query = null;
		
		query = this.em().createQuery(
			    "from AclModules as mod " +
				"where mod.parentId = " + parentId +
				"and mod.tree = '1' " +
			    //"select * from acl_modules " +
				//"where parent_id = " + parentId +				
				
				" "
		);
		
		if (query.getResultList().size() > 0) {
			returns = query.getResultList();	
		}
		return returns;
		/* 
		$select=$this->_db->select()
		->from($this->_tb_modules, array('id','parent_id','module','title','linkable','tree'))
		->from($this->_tb_permissions, array())
		->from($this->_tb_roles, array())
		->from($this->_tb_users, array())
		->where($this->_tb_modules."_id = $this->_tb_modules.id")
		->where($this->_tb_permissions.".{$this->_tb_roles}_id = $this->_tb_roles.id")
		->where($this->_tb_users.".acl_roles_id = $this->_tb_permissions.{$this->_tb_roles}_id")
		->where('parent_id ='.(int)$parent_id)
		->where($this->_tb_users.'.user_name = "' . $this->_user . '"')
		->where($this->_tb_modules.'.tree = ?', '1') //[TODO] externalizar la condicion tree segun el caso

		->group($this->_tb_modules.'.id')
		;

		//Zwei_Utils_Debug::write($select->__toString());
		return($this->_db->fetchAll($select));
		*/
	}
	
	
	
	public List<AclModules> getChildrens(int parentId)
	{
		List<AclModules> childrens = listGrantedResourcesByParentId(parentId);

		if (parentId != 0) {
			for (AclModules child : childrens) {
				
			}
		}
		
		return childrens;
	}


	public HashMap getTreeStruct(int parentId)
	{
		List<AclModules> root = getChildrens(parentId);
		HashMap<Integer, HashMap> nodes = new HashMap(); 
		HashMap subnodes = new HashMap();
		
		//List<List<String>> nodes = new ArrayList<List<String>>();
		
		int i = 0;
		int key = 0;
		for (AclModules branch : root) {
			
			if (branch.getTree().equals("1")) {
				key = (int) ((branch.parentId.id == 0) ? branch.getId() : i);
			}
			
			subnodes = new HashMap();
			subnodes.put("id", branch.getId());
			subnodes.put("label", branch.getTitle());
			
			if (branch.getLinkable().equals("1")) {
				subnodes.put("url", "index/components?p=" + branch.getModule());
			}
			
			if (getChildrens(key) != null) {
				subnodes.put("childrens", getChildrens(key));
				i++;
			}
			nodes.put(key, subnodes);
		}
	    
		return nodes;
	}	
}

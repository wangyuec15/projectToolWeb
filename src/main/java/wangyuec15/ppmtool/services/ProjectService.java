package wangyuec15.ppmtool.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import wangyuec15.ppmtool.domain.Backlog;
import wangyuec15.ppmtool.domain.Project;
import wangyuec15.ppmtool.domain.User;
import wangyuec15.ppmtool.exceptions.ProjectIdException;
import wangyuec15.ppmtool.exceptions.ProjectNotFoundException;
import wangyuec15.ppmtool.repositories.BacklogRepository;
import wangyuec15.ppmtool.repositories.ProjectRepository;
import wangyuec15.ppmtool.repositories.UserRepository;

@Service
public class ProjectService {

	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private BacklogRepository backlogRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	public Project saveOrUpdateProject(Project project, String username) {
		
//		project.findId != null
//		find by db id -> null
		
		if(project.getId()!=null) {
			Project existingProject = projectRepository.findByProjectIdentifier(project.getProjectIdentifier());
			if(existingProject != null &&(!existingProject.getProjectLeader().equals(username))) {
				throw new ProjectNotFoundException("Project not found in your account");
			} else if(existingProject == null) {
				throw new ProjectNotFoundException("Project with ID: '"+project.getProjectIdentifier()+"' cannot be updated because it doesn't exist");
			}
		}
		
		
		try {
			User user = userRepository.findByUsername(username);
			
			project.setUser(user);
			project.setProjectLeader(user.getUsername());
			
			project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
			
			if(project.getId() == null) {
				Backlog backlog = new Backlog();
				project.setBacklog(backlog);
				backlog.setProject(project);
				backlog.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
			}
			if(project.getId() != null) {
				project.setBacklog(backlogRepository.findByProjectIdentifier(project.getProjectIdentifier().toUpperCase()));
			}
			
			return projectRepository.save(project);
		}catch (Exception e){
			throw new ProjectIdException("Project ID '"+project.getProjectIdentifier().toUpperCase()+"' already exists");
		}
	}
	
	public Project findProjectByIdentifier(String projectId, String username) {
		Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());
		
		if(project == null) {
			throw new ProjectIdException("Project ID '"+projectId.toUpperCase()+"' does not exist");
		}
		
		//Only want to return the project if the user looking for it is the owner
		if(!project.getProjectLeader().equals(username)) {
			throw new ProjectNotFoundException("Project not found in your account");
		}
		
		return project;
	}
	
	public Iterable<Project> findAllProjects(String username) {
		return projectRepository.findAllByProjectLeader(username);
	}
	
	public void deleteProjectByIdentifier(String projectid, String username) {
		
		
		projectRepository.delete(findProjectByIdentifier(projectid, username));
	}
	
}

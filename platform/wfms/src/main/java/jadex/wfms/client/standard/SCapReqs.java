package jadex.wfms.client.standard;

import jadex.wfms.service.IAAAService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SCapReqs
{
	public static Set ACTIVITY_HANDLING = new HashSet(Arrays.asList(
			Integer.valueOf[] {
					IAAAService.COMMIT_WORKITEM,
					IAAAService.RELEASE_WORKITEM,
					IAAAService.ADD_ACTIVITY_LISTENER,
					IAAAService.REMOVE_ACTIVITY_LISTENER}));
	
	public static Set WORKITEM_LIST = new HashSet(Arrays.asList(
			Integer.valueOf[] {
					IAAAService.ACQUIRE_WORKITEM,
					IAAAService.ADD_WORKITEM_LISTENER,
					IAAAService.REMOVE_WORKITEM_LISTENER}));
	
	public static Set PROCESS_LIST = new HashSet(Arrays.asList(
			Integer.valueOf[] {
					IAAAService.PD_ADD_REPOSITORY_LISTENER,
					IAAAService.PD_ADD_PROCESS_MODEL,
					IAAAService.PD_REMOVE_PROCESS_MODEL,
					IAAAService.PD_REQUEST_MODEL_PATHS,
					IAAAService.START_PROCESS}));
	
	public static Set ADMIN_ACTIVITIES = new HashSet(Arrays.asList(
			Integer.valueOf[] {
					IAAAService.ADMIN_ADD_ACTIVITIES_LISTENER,
					IAAAService.ADMIN_REMOVE_ACTIVITIES_LISTENER,
					IAAAService.ADMIN_TERMINATE_ACTIVITY}));
}

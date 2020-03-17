package elevatorsplus.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.HumanEntity;

import elevatorsplus.enums.Control;
import elevatorsplus.objects.TextLocation;
import elevatorsplus.objects.sessions.LinkingSession;
import elevatorsplus.objects.sessions.SelectionSession;
import elevatorsplus.objects.sessions.ViewSession;

public class SessionManager {

	private Map<String, SelectionSession> selectionSessions;
	private Map<String, LinkingSession> linkingSessions;
	private Map<HumanEntity, ViewSession> viewSessions;
	
	public SessionManager() {
		this.selectionSessions = new HashMap<>();
		this.linkingSessions = new HashMap<>();
		this.viewSessions = new HashMap<>();
	}
	
	public boolean doneSelectionSession(String player) {
		if(!hasSelectionSession(player)) return false;
		
		selectionSessions.remove(player);
		return true;
	}
	
	public boolean doneLinkingSession(String player) {
		if(!hasLinkingSession(player)) return false;
		
		linkingSessions.remove(player);
		return true;
	}
	
	public void doneViewSession(HumanEntity player) {
		if(!hasViewSession(player)) return;
		viewSessions.remove(player);
	}
	
	public SelectionSession getSelectionSession(String player) {
		return selectionSessions.get(player);
	}
	
	public LinkingSession getLinkingSession(String player) {
		return linkingSessions.get(player);
	}
	
	public ViewSession getViewSession(HumanEntity player) {
		return viewSessions.get(player);
	}
	
	public boolean hasSelectionSession(String player) {
		return selectionSessions.containsKey(player);
	}
	
	public boolean hasLinkingSession(String player) {
		return linkingSessions.containsKey(player);
	}
	
	public boolean hasViewSession(HumanEntity player) {
		return viewSessions.containsKey(player);
	}
	
	public boolean startSelectionSession(String player, String elevator, Control control) {
		if(hasSelectionSession(player)) return false;
		
		SelectionSession session = new SelectionSession(player, elevator, control);
		selectionSessions.put(player, session);
		return true;
	}
	
	public boolean startLinkingSession(String player, String elevator, TextLocation location) {
		if(hasLinkingSession(player)) return false;
		
		LinkingSession session = new LinkingSession(player, elevator, location, false);
		linkingSessions.put(player, session);
		return true;
	}
	
	public boolean startLinkingSession(String player, String elevator, TextLocation location, int current) {
		if(hasLinkingSession(player)) return false;
		
		LinkingSession session = new LinkingSession(player, elevator, location, true, current);
		linkingSessions.put(player, session);
		return true;
	}
	
	public void startViewSession(HumanEntity player, ViewSession session) {
		viewSessions.put(player, session);
	}
	
}

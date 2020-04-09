package elevatorsplus.listener.session;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.HumanEntity;

import elevatorsplus.database.TextLocation;
import elevatorsplus.listener.ElementType;

public class SessionManager {

	private final Map<String, SelectionSession> selectionSessions;
	private final Map<String, LinkingSession> linkingSessions;
	private final Map<HumanEntity, ViewSession> viewSessions;
	
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
	
	public boolean startSelectionSession(String player, String elevator, ElementType element) {
		if(hasSelectionSession(player)) return false;
		
		SelectionSession session = new SelectionSession(player, elevator, element);
		selectionSessions.put(player, session);
		return true;
	}
	
	public boolean startLinkingSession(String player, String elevator, ElementType element, TextLocation location) {
		if(hasLinkingSession(player)) return false;
		
		LinkingSession session = new LinkingSession(player, elevator, element, location, false);
		linkingSessions.put(player, session);
		return true;
	}
	
	public boolean startLinkingSession(String player, String elevator, ElementType element, TextLocation location, int current) {
		if(hasLinkingSession(player)) return false;
		
		LinkingSession session = new LinkingSession(player, elevator, element, location, true, current);
		linkingSessions.put(player, session);
		return true;
	}
	
	public void startViewSession(HumanEntity player, ViewSession session) {
		viewSessions.put(player, session);
	}
	
}

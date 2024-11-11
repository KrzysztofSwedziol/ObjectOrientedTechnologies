package command;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CommandRegistry {

	private ObservableList<Command> commandStack = FXCollections
			.observableArrayList();
	private ObservableList<Command> undoneStack = FXCollections.observableArrayList();

	public void executeCommand(Command command) {
		command.execute();
		commandStack.add(command);
		undoneStack.clear();
	}

	public void redo() {
		if(!undoneStack.isEmpty()) {
			Command command = undoneStack.remove(undoneStack.size() - 1);
			command.redo();
			commandStack.add(command);
		}
	}

	public void undo() {
		if(!commandStack.isEmpty()) {
			Command command = commandStack.remove(commandStack.size()-1);
			command.undo();
			undoneStack.add(command);
		}
	}

	public ObservableList<Command> getCommandStack() {
		return commandStack;
	}
}

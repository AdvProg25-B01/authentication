package id.ac.ui.cs.advprog.authentication.strategy;

public class AdminStrategy implements RoleStrategy {
    @Override
    public void execute() {
        System.out.println("Admin-specific behavior executed.");
    }
}
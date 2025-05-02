package id.ac.ui.cs.advprog.authentication.strategy;

public class KasirStrategy implements RoleStrategy {
    @Override
    public void execute() {
        System.out.println("Kasir-specific behavior executed.");
    }
}
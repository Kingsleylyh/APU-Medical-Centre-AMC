package amc.group.pkg9;

import java.util.List;

public interface PrescriptionProcessor {
    public void processPrescription(List<PrescriptionComponent> components);
    public double calculateTotalCost(List<PrescriptionComponent> components);
    public String generateSummary(List<PrescriptionComponent> components);
}

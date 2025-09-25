package amc.group.pkg9;

import java.io.IOException;
import java.util.List;

public class PrescriptionAmount extends PrescriptionComponent {
    double baseFee=0.0,consultationFee=0.0,medicineCharges=0.0,subtotals=0.0;
    private String startTime, endTime;
    private int duration;

    private static final int includedMins=15;
    private static final int blockMins=15;
    private static final double chargePerBlock=10.0;

    public PrescriptionAmount(String amountId,String appointmentId,double consultationFee,double medicineCharges,double subtotals){
        super(amountId,appointmentId);
        this.consultationFee=consultationFee;
        this.medicineCharges=medicineCharges;
        this.subtotals=calculateCost();
    }

    public PrescriptionAmount(String amountId,String appointmentId,String startTime,String endTime,double baseFee,double medicineCharges){
        super(amountId,appointmentId);
        this.startTime=startTime;
        this.endTime=endTime;
        this.baseFee=baseFee;
        this.duration=calculateDuration(startTime,endTime);
        this.consultationFee=calculateConsultationFee();
        this.medicineCharges=medicineCharges;
        this.subtotals=calculateCost();
    }

    @Override
    public double calculateCost(){
        return consultationFee+medicineCharges;
    }

    private double calculateConsultationFee(){
        if(duration<=includedMins){
            return baseFee;
        } else {
            int additionalBlocks=(int)Math.ceil((double)(duration-includedMins)/blockMins);
            return baseFee+(additionalBlocks*chargePerBlock);
        }
    }

    public int calculateDuration(String startTime,String endTime){
        if(startTime==null||startTime.isEmpty()){
            return 0;
        }
        if(endTime==null||endTime.isEmpty()){
            return 0;
        }
        try{
            String[] startPart=startTime.split(":");
            int startHour=Integer.parseInt(startPart[0]);
            int startMinute=Integer.parseInt(startPart[1]);

            String[] endPart=endTime.split(":");
            int endHour=Integer.parseInt(endPart[0]);
            int endMinute=Integer.parseInt(endPart[1]);

            int startTotalMins=startHour*60+startMinute;
            int endTotalMins=endHour*60+endMinute;

            int duration=endTotalMins-startTotalMins;

            return duration;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public String getDisplayInfo(){
        if (duration > 0) {
            return String.format("Consultation (%d mins): RM%.2f, Medicine: RM%.2f, Total: RM%.2f",
                    duration, consultationFee, medicineCharges, subtotals);
        } else {
            return String.format("Consultation: RM%.2f, Medicine: RM%.2f, Total: RM%.2f",
                    consultationFee, medicineCharges, subtotals);
        }
    }

    @Override
    public String getComponentType(){
        return "PAYMENT_SUMMARY";
    }

    public void updateAppointmentTimes(String startTime,String endTime){
        this.startTime=startTime;
        this.endTime=endTime;
        this.duration=calculateDuration(startTime,endTime);
        this.consultationFee=calculateConsultationFee();
        this.subtotals=calculateCost();
    }

    public void setConsultationFee(double consultationFee){
        this.consultationFee=consultationFee;
        this.subtotals=calculateCost();
    }

    public void setBaseFee(double baseFee){
        this.baseFee=baseFee;
        this.consultationFee=calculateConsultationFee();
        this.subtotals=calculateCost();
    }

    public void setMedicineCharges(double medicineCharges) {
        this.medicineCharges = medicineCharges;
        this.subtotals=calculateCost();
    }

    public void setEndTime(){
        this.endTime=endTime;
        this.duration=calculateDuration(startTime,endTime);
        this.consultationFee=calculateConsultationFee();
        this.subtotals=calculateCost();
    }

    public double getBaseFee() {
        return baseFee;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getDuration() {
        return duration;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public double getMedicineCharges() {
        return medicineCharges;
    }

    public double getSubtotals() {
        return subtotals;
    }

    public static String getNextId(){
        int maxId = 0;
        try{
            List<PrescriptionAmount> amounts=DoctorFileManager.loadPrescriptionAmount();
            for(PrescriptionAmount amount:amounts){
                int id = Integer.parseInt(amount.getId().substring(3));
                if (id > maxId)
                    maxId = id;
            }
        } catch (IOException e) {
            System.err.println("Error loading prescription amounts: " + e.getMessage());
        }
        return String.format("AMT%03d", maxId + 1);
    }

    public String getSummary(){
        if(duration<=includedMins){
            return String.format("Consultation (%d minutes): RM%.2f (base fee)",
                    duration, consultationFee);
        } else{
            int additionalBlocks=(int)Math.ceil((double)(duration-includedMins)/blockMins);
            double extraCharge=additionalBlocks*chargePerBlock;
            return String.format("Consultation (%d minutes):\n" +
                    "- Base fee: RM%.2f\n" +
                    "- Additional time (%d blocks): RM%.2f\n" +
                    "- Total consultation: RM%.2f",
                    duration, baseFee, additionalBlocks, extraCharge, consultationFee);
        }
    }

    @Override
    public String toString(){
        return String.format("%s|%s|%.2f|%.2f|%.2f",
            super.getId(),super.getAppointmentId(),
            consultationFee,medicineCharges,subtotals);
    }
}

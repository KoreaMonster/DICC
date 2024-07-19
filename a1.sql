CREATE TABLE appointments (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    patient_name VARCHAR2(100),
    caregiver_name VARCHAR2(100),
    appointment_date TIMESTAMP,
    notes VARCHAR2(1000)
);
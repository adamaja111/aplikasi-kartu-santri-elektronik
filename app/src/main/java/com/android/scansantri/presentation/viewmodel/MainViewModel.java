package com.android.scansantri.presentation.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.scansantri.data.model.Achievement;
import com.android.scansantri.data.model.BackupData;
import com.android.scansantri.data.model.HealthRecord;
import com.android.scansantri.data.model.Organization;
import com.android.scansantri.data.model.ParentData;
import com.android.scansantri.data.model.SantriAchievement;
import com.android.scansantri.data.model.SantriData;
import com.android.scansantri.data.model.SantriHealthRecord;
import com.android.scansantri.data.model.SantriOrganization;
import com.android.scansantri.data.model.SantriViolation;
import com.android.scansantri.data.model.Violation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference santriRef = database.getReference("Santri");
    private final DatabaseReference parentRef = database.getReference("SantriParent");
    private final DatabaseReference organisasiRef = database.getReference("SantriOrganizations");
    private final DatabaseReference prestasiRef = database.getReference("SantriAchievements");
    private final DatabaseReference healthRef = database.getReference("SantriHealthRecord");
    private final DatabaseReference pelanggaranRef = database.getReference("SantriViolations");


    private final MutableLiveData<List<SantriData>> _santriList = new MutableLiveData<>();
    public LiveData<List<SantriData>> getSantriList() {
        return _santriList;
    }

    private final MutableLiveData<List<ParentData>> _parentList = new MutableLiveData<>();
    public LiveData<List<ParentData>> getParentList() {
        return _parentList;
    }

    private final MutableLiveData<List<SantriOrganization>> _organizationList = new MutableLiveData<>();
    public LiveData<List<SantriOrganization>> getOrganizationList() {
        return _organizationList;
    }

    private final MutableLiveData<List<SantriAchievement>> _achievementList = new MutableLiveData<>();
    public LiveData<List<SantriAchievement>> getAchievementList() {
        return _achievementList;
    }

    private final MutableLiveData<List<SantriHealthRecord>> _healthRecordList = new MutableLiveData<>();
    public LiveData<List<SantriHealthRecord>> getHealthRecordList() {
        return _healthRecordList;
    }

    private final MutableLiveData<List<SantriViolation>> _violationList = new MutableLiveData<>();
    public LiveData<List<SantriViolation>> getViolationList() {
        return _violationList;
    }

    private final MutableLiveData<Boolean> _loading = new MutableLiveData<>();
    public LiveData<Boolean> getLoading() {
        return _loading;
    }

    private final MutableLiveData<String> _errorMsg = new MutableLiveData<>();
    public LiveData<String> getErrorMsg() {
        return _errorMsg;
    }

    private MediatorLiveData<Boolean> isDataReady = new MediatorLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);

        isDataReady.addSource(getParentList(), parentList -> checkIfDataReady());
        isDataReady.addSource(getOrganizationList(), organizationList -> checkIfDataReady());
        isDataReady.addSource(getAchievementList(), achievementList -> checkIfDataReady());
        isDataReady.addSource(getHealthRecordList(), healthList -> checkIfDataReady());
        isDataReady.addSource(getViolationList(), violationList -> checkIfDataReady());

        getListSantri();
        getParentListDatabase();
        getOrganizationsListDatabase();
        getAchievementsListDatabase();
        getHealthRecordsListDatabase();
        getViolationListDatabase();
    }

    private void checkIfDataReady() {
        if (getParentList().getValue() != null &&
                getOrganizationList().getValue() != null &&
                getAchievementList().getValue() != null &&
                getHealthRecordList().getValue() != null &&
                getViolationList().getValue() != null) {

            isDataReady.setValue(true);
        }
    }

    public LiveData<Boolean> isDataReady() {
        return isDataReady;
    }

    public void getParentListDatabase() {
        _loading.setValue(true);
        parentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ParentData> listParentDatabase = new ArrayList<>();
                Log.e("MainViewModel", "inside onDataChange parentRef");
                for (DataSnapshot parentSnapshot : snapshot.getChildren()) {
                    ParentData parentDariDatabase = parentSnapshot.getValue(ParentData.class);
                    Log.e("MainViewModel", "inside parentSnapshot " + parentDariDatabase);
                    if (parentDariDatabase != null) {
                        listParentDatabase.add(parentDariDatabase);
                    }
                }
                _loading.setValue(false);
                _parentList.setValue(listParentDatabase);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                _errorMsg.setValue(error.getMessage());
                _loading.setValue(false);
            }
        });
    }

    public void getViolationListDatabase() {
        _loading.setValue(true);
        pelanggaranRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<SantriViolation> listSantriViolation = new ArrayList<>();

                for (DataSnapshot santriSnapshot : snapshot.getChildren()) {
                    String santriUid = santriSnapshot.getKey();

                    for (DataSnapshot violationSnapshot : santriSnapshot.getChildren()) {
                        String violationId = violationSnapshot.getKey();
                        Violation violationDariDatabase = violationSnapshot.getValue(Violation.class);

                        if (violationDariDatabase != null) {
                            SantriViolation santriViolation = new SantriViolation();
                            santriViolation.setSantriUid(santriUid);
                            santriViolation.setViolationId(violationId);
                            santriViolation.setViolation(violationDariDatabase);

                            listSantriViolation.add(santriViolation);
                        }
                    }
                }

                _loading.setValue(false);
                _violationList.setValue(listSantriViolation);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                _errorMsg.setValue(error.getMessage());
                _loading.setValue(false);
            }
        });
    }

    public void getHealthRecordsListDatabase() {
        _loading.setValue(true);
        healthRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<SantriHealthRecord> listSantriHealthRecord = new ArrayList<>();

                for (DataSnapshot santriSnapshot : snapshot.getChildren()) {
                    String santriUid = santriSnapshot.getKey();

                    for (DataSnapshot healthSnapshot : santriSnapshot.getChildren()) {
                        String healthId = healthSnapshot.getKey();
                        HealthRecord healthRecordDariDatabase = healthSnapshot.getValue(HealthRecord.class);

                        if (healthRecordDariDatabase != null) {
                            SantriHealthRecord santriHealthRecord = new SantriHealthRecord();
                            santriHealthRecord.setSantriUid(santriUid);
                            santriHealthRecord.setHealthRecordId(healthId);
                            santriHealthRecord.setHealthRecord(healthRecordDariDatabase);

                            listSantriHealthRecord.add(santriHealthRecord);
                        }
                    }
                }

                _loading.setValue(false);
                _healthRecordList.setValue(listSantriHealthRecord);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                _errorMsg.setValue(error.getMessage());
                _loading.setValue(false);
            }
        });
    }

    public void getAchievementsListDatabase() {
        _loading.setValue(true);
        prestasiRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<SantriAchievement> listSantriAchievement = new ArrayList<>();

                for (DataSnapshot santriSnapshot : snapshot.getChildren()) {
                    String santriUid = santriSnapshot.getKey();

                    for (DataSnapshot prestasiSnapshot : santriSnapshot.getChildren()) {
                        String prestasiId = prestasiSnapshot.getKey();
                        Achievement prestasiDariDatabase = prestasiSnapshot.getValue(Achievement.class);

                        if (prestasiDariDatabase != null) {
                            SantriAchievement santriAchievement = new SantriAchievement();
                            santriAchievement.setSantriUid(santriUid);
                            santriAchievement.setAchievementId(prestasiId);
                            santriAchievement.setAchievement(prestasiDariDatabase);

                            listSantriAchievement.add(santriAchievement);
                        }
                    }
                }

                _loading.setValue(false);
                _achievementList.setValue(listSantriAchievement);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                _errorMsg.setValue(error.getMessage());
                _loading.setValue(false);
            }
        });
    }

    public void getOrganizationsListDatabase() {
        _loading.setValue(true);
        organisasiRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<SantriOrganization> listSantriOrganisasi = new ArrayList<>();

                for (DataSnapshot santriSnapshot : snapshot.getChildren()) {
                    String santriUid = santriSnapshot.getKey(); // Get the santriUid

                    for (DataSnapshot organizationSnapshot : santriSnapshot.getChildren()) {
                        String organizationId = organizationSnapshot.getKey(); // Get the organizationId
                        Organization organisasiDariDatabase = organizationSnapshot.getValue(Organization.class);

                        if (organisasiDariDatabase != null) {
                            SantriOrganization santriOrganization = new SantriOrganization();
                            santriOrganization.setSantriUid(santriUid);
                            santriOrganization.setOrganizationId(organizationId);
                            santriOrganization.setOrganization(organisasiDariDatabase);

                            listSantriOrganisasi.add(santriOrganization);
                        }
                    }
                }

                _loading.setValue(false);
                _organizationList.setValue(listSantriOrganisasi);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                _errorMsg.setValue(error.getMessage());
                _loading.setValue(false);
            }
        });
    }

    public void getListSantri() {
        _loading.setValue(true);
        santriRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<SantriData> listSantriDatabase = new ArrayList<>();
                for (DataSnapshot santriSnapshot : snapshot.getChildren()) {
                    SantriData santriDariDatabase = santriSnapshot.getValue(SantriData.class);
                    if (santriDariDatabase != null) {
                        listSantriDatabase.add(santriDariDatabase);
                    }
                }
                _loading.setValue(false);
                _santriList.setValue(listSantriDatabase);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                _errorMsg.setValue(error.getMessage());
                _loading.setValue(false);
            }
        });
    }

    public void restoreDatabase(BackupData backupData) {
        _loading.setValue(true);

        // Restore Santri
        if (backupData.getSantriList() != null) {
            santriRef.removeValue();
            for (SantriData s : backupData.getSantriList()) {
                santriRef.child(s.getUid()).setValue(s);
            }
        }

        // Restore Parents
        if (backupData.getParentList() != null) {
            parentRef.removeValue();
            for (ParentData p : backupData.getParentList()) {
                parentRef.child(p.getSantriId()).setValue(p);
            }
        }

        // Restore Organizations (Nested)
        if (backupData.getOrganizationList() != null) {
            organisasiRef.removeValue();
            for (SantriOrganization so : backupData.getOrganizationList()) {
                organisasiRef.child(so.getSantriUid()).child(so.getOrganizationId()).setValue(so.getOrganization());
            }
        }

        // Restore Achievements (Nested)
        if (backupData.getAchievementList() != null) {
            prestasiRef.removeValue();
            for (SantriAchievement sa : backupData.getAchievementList()) {
                prestasiRef.child(sa.getSantriUid()).child(sa.getAchievementId()).setValue(sa.getAchievement());
            }
        }

        // Restore Health Records (Nested)
        if (backupData.getHealthRecordList() != null) {
            healthRef.removeValue();
            for (SantriHealthRecord shr : backupData.getHealthRecordList()) {
                healthRef.child(shr.getSantriUid()).child(shr.getHealthRecordId()).setValue(shr.getHealthRecord());
            }
        }

        // Restore Violations (Nested)
        if (backupData.getViolationList() != null) {
            pelanggaranRef.removeValue();
            for (SantriViolation sv : backupData.getViolationList()) {
                pelanggaranRef.child(sv.getSantriUid()).child(sv.getViolationId()).setValue(sv.getViolation());
            }
        }

        _loading.setValue(false);
    }
}

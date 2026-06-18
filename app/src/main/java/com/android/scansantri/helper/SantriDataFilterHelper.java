package com.android.scansantri.helper;

import com.android.scansantri.data.model.Achievement;
import com.android.scansantri.data.model.HealthRecord;
import com.android.scansantri.data.model.Organization;
import com.android.scansantri.data.model.ParentData;
import com.android.scansantri.data.model.SantriAchievement;
import com.android.scansantri.data.model.SantriData;
import com.android.scansantri.data.model.SantriHealthRecord;
import com.android.scansantri.data.model.SantriOrganization;
import com.android.scansantri.data.model.SantriViolation;
import com.android.scansantri.data.model.Violation;

import java.util.ArrayList;
import java.util.List;

public class SantriDataFilterHelper {

    public static ParentData findParentDataBySantriId(List<ParentData> listParentSantri, String uid) {
        for (ParentData parentData : listParentSantri) {
            if (parentData.getSantriId().equals(uid)) {
                return parentData;
            }
        }
        return null;
    }

    public static SantriData findSantriDataByNis(List<SantriData> listSantri, String uid) {
        for (SantriData parentData : listSantri) {
            if (parentData.getNis().equals(uid)) {
                return parentData;
            }
        }
        return null;
    }

    public static List<Violation> filterViolationsBySantri(List<SantriViolation> listSemuaPelanggaran, String santriUid) {
        List<Violation> filteredList = new ArrayList<>();
        for (SantriViolation santriViolation : listSemuaPelanggaran) {
            if (santriViolation.getSantriUid().equals(santriUid)) {
                filteredList.add(santriViolation.getViolation());
            }
        }
        return filteredList;
    }

    public static List<HealthRecord> filterHealthRecordsBySantri(List<SantriHealthRecord> listSemuaHealth, String santriUid) {
        List<HealthRecord> filteredList = new ArrayList<>();
        for (SantriHealthRecord santriHealthRecord : listSemuaHealth) {
            if (santriHealthRecord.getSantriUid().equals(santriUid)) {
                filteredList.add(santriHealthRecord.getHealthRecord());
            }
        }
        return filteredList;
    }

    public static List<Organization> filterOrganizationBySantri(List<SantriOrganization> listSemuaOrganisasi, String santriUid) {
        List<Organization> filteredList = new ArrayList<>();
        for (SantriOrganization santriOrganization : listSemuaOrganisasi) {
            if (santriOrganization.getSantriUid().equals(santriUid)) {
                filteredList.add(santriOrganization.getOrganization());
            }
        }
        return filteredList;
    }

    public static List<Achievement> filterAchievementBySantri(List<SantriAchievement> listSemuaPrestasi, String santriUid) {
        List<Achievement> filteredList = new ArrayList<>();
        for (SantriAchievement santriAchievement : listSemuaPrestasi) {
            if (santriAchievement.getSantriUid().equals(santriUid)) {
                filteredList.add(santriAchievement.getAchievement());
            }
        }
        return filteredList;
    }
}

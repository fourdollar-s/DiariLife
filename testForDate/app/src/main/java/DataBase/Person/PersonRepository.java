package DataBase.Person;

import android.app.Application;
import android.provider.ContactsContract;

import androidx.room.Room;

import DataBase.Building.DataUao;

import java.util.List;

public class PersonRepository {
    private PersonDao personDao;
    private List<Person> people;
    private int count;
    //----------------------

    PersonRepository(Application application) {
        DiaryDatabase diaryDatabase = DiaryDatabase.getInstance(application);
        //PersonRoomDatabase personRoomDatabase = PersonRoomDatabase.getDatabase(application);
        personDao = diaryDatabase.personDao();
        count = personDao.getPersonCount();
    }

    //------------person--------------------------------------------------
    List<Person> getPeople(String d) {
        return personDao.getAllPerson(d);
    }

    int getCount() {
        return count;
    }

    void insert(Person person) {
        DiaryDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                personDao.insert(person);
            }
        });
    }

    void update(Person person) {
        DiaryDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                personDao.update(person);
            }
        });
    }

    void delete() {
        DiaryDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                personDao.delete();
            }
        });
    }

    void updatePersonCloth(int clothType,int pantType, int faceType, int hairType,int accessoriesType,int diaryId){
        DiaryDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                personDao.updatePersonCloth(clothType,pantType,faceType,hairType,accessoriesType,diaryId);
            }
        });
    }

    void updatePersonText(String text,int diaryId){
        DiaryDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                personDao.updatePersonText(text,diaryId);
            }
        });
    }
}
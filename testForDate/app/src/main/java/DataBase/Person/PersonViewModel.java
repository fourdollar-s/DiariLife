package DataBase.Person;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import java.util.List;

public class PersonViewModel extends AndroidViewModel {

    private PersonRepository personRepository;
    private List<Person> people;
    private int count;

    public PersonViewModel(Application application){
        super(application);
        personRepository=new PersonRepository(application);
        count=personRepository.getCount();
    }

    public  List<Person> getPeople(String d){
        return personRepository.getPeople(d);
    }
    public int count(){
        return count;
    }

    public void insert(Person person){
        personRepository.insert(person);
    }

    public void update(Person person){
        personRepository.update(person);
    }

    public void delete(){
        personRepository.delete();
    }

    public void updateClothType(int clothType,int pantType,int faceType, int hairType, int accessoriesType, int diaryID){
        personRepository.updatePersonCloth(clothType,pantType,faceType,hairType,accessoriesType,diaryID);
    }

    public void updatePersonText(String text, int diaryID){
        personRepository.updatePersonText(text,diaryID);
    }
}

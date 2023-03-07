package seedu.address.storage.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Person;
import seedu.address.model.person.fields.*;
import seedu.address.model.tag.Tag;
import seedu.address.model.user.User;
import seedu.address.storage.addressbook.JsonAdaptedNusMod;
import seedu.address.storage.addressbook.JsonAdaptedTag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JsonAdaptedUser {
    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final String race;
    private final String major;
    private final String gender;
    private final String comms;

    private final String isFavorite;

    private final List<JsonAdaptedNusMod> modules = new ArrayList<>();
    private final List<JsonAdaptedTag> tagged = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedUser(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
                             @JsonProperty("email") String email, @JsonProperty("address") String address,
                             @JsonProperty("race") String race,
                             @JsonProperty("major") String major, @JsonProperty("gender") String gender,
                             @JsonProperty("comms") String comms,
                             @JsonProperty("modules") List<JsonAdaptedNusMod> modules,
                             @JsonProperty("tagged") List<JsonAdaptedTag> tagged,
                             @JsonProperty("favorite") String isFavorite) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.race = race;
        this.major = major;

        this.gender = gender;
        this.comms = comms;
        this.isFavorite = isFavorite;

        if (tagged != null) {
            this.tagged.addAll(tagged);
        }
        if (modules != null) {
            this.modules.addAll(modules);
        }
    }

    public JsonAdaptedUser(User source) {
        this.name = source.getName().fullName;
        this.phone = source.getPhone().value;
        this.email = source.getEmail().value;
        this.address = source.getAddress().value;
        this.race = source.getRace().race;
        this.major = source.getMajor().majorName;
        this.gender = source.getGender().gender.toString();
        this.comms = source.getComms().nameOfCommunicationChannel;
        this.modules.addAll(source.getModules().mods.stream()
                .map(mod -> (new JsonAdaptedNusMod(mod.name)))
                .collect(Collectors.toList()));
        this.tagged.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
        this.isFavorite = source.getIsFavorite().toString();
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public User toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();

        for (JsonAdaptedTag tag : tagged) {
            personTags.add(tag.toModelType());
        }

        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(phone);

        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = new Email(email);

        if (!Address.isValidAddress(address)) {
            throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
        }
        final Address modelAddress = new Address(address);

        if (!Gender.isValidGender(this.gender)) {
            throw new IllegalValueException(Gender.MESSAGE_CONSTRAINTS);
        }
        final Gender modelGender = new Gender(this.gender);

        if (!Major.isValidMajor(this.major)) {
            throw new IllegalValueException(Major.MESSAGE_CONSTRAINTS);
        }
        final Major modelMajor = new Major(this.major);

        if (!Race.isValidRace(address)) {
            throw new IllegalValueException(Race.MESSAGE_CONSTRAINTS);
        }
        final Race modelRace = new Race(this.race);

        if (!Favorite.isValidFavorite(isFavorite)) {
            throw new IllegalValueException(Favorite.MESSAGE_CONSTRAINTS);
        }
        final Favorite favoriteStatus = new Favorite(this.isFavorite);

        Set<NusMod> personMods = new HashSet<>();
        for (JsonAdaptedNusMod mod: this.modules) {
            personMods.add(mod.toModelType());
        }
        Modules modelModules = new Modules(personMods);

        CommunicationChannel modelComms = new CommunicationChannel(this.comms);

        final Set<Tag> modelTags = new HashSet<>(personTags);
        return new User(modelName, modelPhone, modelEmail, modelAddress, modelGender, modelMajor,
                modelModules, modelRace, modelTags, modelComms, favoriteStatus);
    }
}

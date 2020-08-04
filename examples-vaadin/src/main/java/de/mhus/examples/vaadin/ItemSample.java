package de.mhus.examples.vaadin;

import java.util.Date;
import java.util.UUID;

import de.mhus.lib.annotations.form.ALayoutModel;
import de.mhus.lib.annotations.pojo.Action;
import de.mhus.lib.annotations.vaadin.Align;
import de.mhus.lib.annotations.vaadin.Column;
import de.mhus.lib.core.MSystem;
import de.mhus.lib.core.definition.DefAttribute;
import de.mhus.lib.core.definition.DefRoot;
import de.mhus.lib.errors.MException;
import de.mhus.lib.form.definition.FaItemDefinition;
import de.mhus.lib.form.definition.FaReadOnly;
import de.mhus.lib.form.definition.FaShowInformationPanel;
import de.mhus.lib.form.definition.FmCheckbox;
import de.mhus.lib.form.definition.FmCombobox;
import de.mhus.lib.form.definition.FmDate;
import de.mhus.lib.form.definition.FmDate.FORMATS;
import de.mhus.lib.form.definition.FmNumber;
import de.mhus.lib.form.definition.FmNumber.TYPES;
import de.mhus.lib.form.definition.FmRichText;
import de.mhus.lib.form.definition.FmText;
import de.mhus.lib.form.definition.FmTextArea;

public class ItemSample {

    public ItemSample(int i) {
        initials = "Mr";
        name = "name" + i;
        id = UUID.randomUUID().toString();
        description = "Description " + i;
    }

    public ItemSample(ItemSample org) {
        this.name = org.name;
        this.id = org.id;
        this.initials = org.initials;
        this.description = org.description;
        // ...
    }

    @Column(order=1,title="Kürzel", editable=false)
    public String initials;
    @Column(order=2,title="Name", editable=false)
    public String name;
    @Column(order=3,title="Termin (Suchformat: 2019-12-31)", editable=false)
    public Date eventDate;
    @Column(order=4,title="Status", editable=false)
    public String status;
    @Column(order=5,title="Kurstyp", editable=false)
    public String type;
    @Column(order=6,title="Aktivierung", editable=false)
    public Date activationDate;
    @Column(order=7,title="Plätze", editable=false)
    public Integer seats;
    @Column(order=8,title="Reservierte Plätze", editable=false)
    public Integer seatsBooked;
    @Column(order=9,title="Dauer", editable=false)
    public Integer duration;
    @Column(order=10,title="Ort", editable=false)
    public String location;
    @Column(order=11,title="Informations Seite", elapsed=false, editable=false)
    public String infoPage;
    @Column(order=12,title="Buchbar", editable=false, align=Align.CENTER)
    public Boolean bookable;
    @Column(order=13,title="Automatische Buchung", editable=false, align=Align.CENTER)
    public Boolean autoActivate;
    @Column(order=14,title="Anbieter", editable=false)
    public String topic;
    @Column(order=15,title="Kurs ID", elapsed=false, editable=false)
    public String id;
    @Column(order=16,title="Erstellt am", elapsed=false, editable=false)
    public Date creationDate;
    @Column(order=17,title="Geändert am", editable=false)
    public Date modifyDate;
    @Column(order=18,title="Beschreibung", elapsed=false, editable=false)
    public String description;
    @Column(order=19,title="Integration", elapsed=false, editable=false)
    public String integration;
    @Column(order=20,title="Parameters", elapsed=false, editable=false)
    public String parameters;
    @Column(order=21,title="Dokumenten Link", elapsed=false, editable=false)
    public String documentsLink;
    @Column(order=22,title="Fremdwebinar Link", elapsed=false, editable=false)
    public String extWebinarLink;

    // create model
    @ALayoutModel
    @Action
    public DefRoot createModel() throws MException {
        
        StringBuilder topicList = new StringBuilder().append("=Bitte wählen;");
        StringBuilder integratorsList = new StringBuilder().append("=Bitte wählen;");

        return new DefRoot(
                new FaShowInformationPanel(),
                new FmText("name", "Name", "Der Name"),
                new FmRichText("description", "Beschreibung", "Die Beschreibung"),
                new FmNumber("seats", TYPES.INTEGER, "Plätze", "Gesamtzahl der Plätze"),
                new FmNumber("seatsbooked", TYPES.INTEGER, "Reservierte Plätze", "Zahl der reservierten Plätze"),
                new FmDate("eventdate", FORMATS.DATETIME, "Termin", "Der Termin"),
                new FmNumber("duration", TYPES.INTEGER, "Dauer", "Dauer in Minuten"),
                new FmText("location", "Ort", "Der Ort"),
                new FmText("infopage", "Informations Seite", "Link au die Informationsseite des Kurses"),
                new FmText("documentslink", "Dokumenten Link", "Link auf die Dokumentenablage des Kurses"),
                new FmCheckbox("bookable", "Buchbar", "Buchbar auch von Studenten, nicht nur von Administratoren"),
                new FmCheckbox("autoactivate", "Automatische Buchung", "Buchung muss nicht vom Support freigegeben werden. Falls keine Plätze frei sind, wird nicht automatisch gebucht."),
                new FmCombobox("status", "Status", "Nur Kurse im Status Aktiv können gebucht werden.", new DefAttribute("items","=Bitte wählen;NEW=Neu;READY=Bereit;ACTIVE=Aktiv;FINISHED=Beendet;ARCHIVED=Archiviert")),
                new FmDate("activationdate", FORMATS.DATE, "Aktivierung", "An diesm Tag wird der Kurs auf Aktiv gestellt. Er muss bereits auf Bereit stehen damit die Aktivierung durchgeführt werden kann."),
                new FmCombobox("topic", "Anbieter", "Der Anbieter", new DefAttribute("items",topicList.toString())),
                new FmText("type", "Kurstyp", "Der Kurstyp, hat Auswirkungen auf das Verhalten des Kurses.", new FaReadOnly()),
                new FmCombobox("integration", "Integration", "Welche Anwendung wird integriert", new DefAttribute("items",integratorsList.toString())),
                new FmCombobox("integrationgoto", "Webinar","Welches GoTo Webinar ist mit dem Kurs verknüpft",new FaItemDefinition("itemsgoto")),
                new FmCombobox("integrationwbt", "WBT","Welches WBT ist mit dem Kurs verknüpft",new FaItemDefinition("itemswbt")),
                new FmText("extwebinarlink", "Fremdwebinar Link", "Link zum Fremdwebinar"),
                new FmTextArea("parameters", "Integrator Parameter", "Konfiguration der Anwendung ist abhängig von der Integration")
                );
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other instanceof ItemSample) {
            return MSystem.equals(initials, ((ItemSample)other).initials) &&
                    MSystem.equals(name, ((ItemSample)other).name) &&
                    MSystem.equals(description, ((ItemSample)other).description) &&
                    MSystem.equals(seats, ((ItemSample)other).seats) &&
                    MSystem.equals(seatsBooked, ((ItemSample)other).seatsBooked) &&
                    MSystem.equals(eventDate, ((ItemSample)other).eventDate) &&
                    MSystem.equals(duration, ((ItemSample)other).duration) &&
                    MSystem.equals(location, ((ItemSample)other).location) &&
                    MSystem.equals(infoPage, ((ItemSample)other).infoPage) &&
                    MSystem.equals(bookable, ((ItemSample)other).bookable) &&
                    MSystem.equals(autoActivate, ((ItemSample)other).autoActivate) &&
                    MSystem.equals(status, ((ItemSample)other).status) &&
                    MSystem.equals(topic, ((ItemSample)other).topic) &&
                    MSystem.equals(integration, ((ItemSample)other).integration) &&
                    MSystem.equals(id, ((ItemSample)other).id) &&
                    MSystem.equals(creationDate, ((ItemSample)other).creationDate) &&
                    MSystem.equals(modifyDate, ((ItemSample)other).modifyDate);
        }
        return super.equals(other);
    }
    
    @Override
    public String toString() {
        return this.getClass().getCanonicalName() + " [" + initials + ", " + name + ", " + seats + ", " + seatsBooked + ", " + location + ", " + eventDate + ", " + status + ", " + id + "]";
    }

}

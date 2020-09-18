package be.ipl.pae.config;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ConverteurJson {

  /**
   * convertis un objet Date en string pour serialisation et deserialise en objet de type Date.
   */
  public static Converter<Date> getConverteurDateJson() {

    Converter<Date> converteur = new Converter<Date>() {
      @Override
      public void serialize(Date obj, ObjectWriter writer, Context ctx) throws Exception {
        if (obj == null) {
          writer.writeNull();
          return;
        }
        writer.writeValue(String.valueOf(obj));
      }

      @Override
      public Date deserialize(ObjectReader reader, Context ctx) {
        Long value = reader.valueAsLong();
        Date sqlDate = new Date(value);
        return sqlDate;
      }
    };
    return converteur;
  }

  /**
   * convertis un objet Timestamp en string pour serialisation et deserialise en objet de type.
   * Timestamp.
   * 
   * @exception Exception
   */
  public static Converter<Timestamp> getConverteurTimestampJson() {
    Converter<Timestamp> converteur = new Converter<Timestamp>() {
      @Override
      public void serialize(Timestamp obj, ObjectWriter writer, Context ctx) throws Exception {
        if (obj == null) {
          writer.writeNull();
          return;
        }
        writer.writeValue(String.valueOf(obj));
      }

      @Override
      public Timestamp deserialize(ObjectReader reader, Context ctx) {
        Long value = reader.valueAsLong();
        Timestamp sqlTimestamp = new Timestamp(value);
        return sqlTimestamp;
      }
    };
    return converteur;
  }

  /**
   * Convertis un objet LocalDate en un String, permet de ne pas sérialiser l'objet LocalDate.
   * 
   */
  public static Converter<LocalDate> getConverteurLocalDateJson() {

    Converter<LocalDate> converteur = new Converter<LocalDate>() {

      @Override
      public void serialize(LocalDate object, ObjectWriter writer, Context ctx) throws Exception {
        // DateTimeFormatter.ofPattern("dd/MM/yyyy")
        writer.writeString(object.format(DateTimeFormatter.ISO_LOCAL_DATE));
      }

      @Override
      public LocalDate deserialize(ObjectReader reader, Context ctx) throws Exception {
        // TODO Auto-generated method stub
        return null;
      }
    };
    return converteur;
  }
}

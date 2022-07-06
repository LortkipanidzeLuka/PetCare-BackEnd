package ge.edu.freeuni.petcarebackend.repository;

import com.querydsl.core.types.dsl.*;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

public final class QueryUtils {

    public static BooleanExpression where(BooleanExpression... predicates) {
        return Optional.ofNullable(predicates)
                .map(Arrays::asList)
                .orElseGet(List::of)
                .stream()
                .reduce(True(), BooleanExpression::and);
    }

    public static ComparableExpressionBase<?>[] groupBy(ComparableExpressionBase<?>... predicates) {
        return Stream.of(predicates)
                .filter(Objects::nonNull)
                .toArray(ComparableExpressionBase<?>[]::new);
    }

    public static BooleanExpression or(BooleanExpression... predicates) {
        return Optional.ofNullable(predicates)
                .map(Arrays::asList)
                .filter(list -> !list.isEmpty())
                .orElseGet(() -> List.of(True()))
                .stream()
                .reduce(False(), BooleanExpression::or);
    }

    public static BooleanExpression boolEq(BooleanPath booleanField, Boolean b) {
        return b == null ? True() : booleanField.eq(b);
    }

    public static BooleanExpression stringEq(StringPath stringField, String s) {
        return StringUtils.isBlank(s) ? True() : stringField.eq(s.trim());
    }

    public static BooleanExpression stringLike(StringPath stringField, String s) {
        return StringUtils.isBlank(s) ? True() : stringField.toLowerCase().like('%' + s.toLowerCase().trim() + '%');
    }

    public static BooleanExpression shortMoreOrEq(NumberPath<Short> ShortField, Short i) {
        return i == null ? True() : ShortField.goe(i);
    }

    public static BooleanExpression shortLessOrEq(NumberPath<Short> ShortField, Short i) {
        return i == null ? True() : ShortField.loe(i);
    }

    public static BooleanExpression longEq(NumberPath<Long> longField, Long l) {
        return l == null ? True() : longField.eq(l);
    }

    public static BooleanExpression dateEq(DateTimePath<Date> timeField, Date time) {
        return time == null ? True() : timeField.eq(time);
    }

    public static BooleanExpression dateLess(DateTimePath<Date> timeField, Date time) {
        return time == null ? True() : timeField.before(time);
    }

    public static BooleanExpression dateLessOrEq(DateTimePath<Date> timeField, Date time) {
        return time == null ? True() : timeField.before(time).or(timeField.eq(time));
    }

    public static BooleanExpression dateMore(DateTimePath<Date> timeField, Date time) {
        return time == null ? True() : timeField.after(time);
    }

    public static BooleanExpression dateMoreOrEq(DateTimePath<Date> timeField, Date time) {
        return time == null ? True() : timeField.after(time).or(timeField.eq(time));
    }

    public static BooleanExpression localDateLess(DatePath<LocalDate> dateField, LocalDate date) {
        return date == null ? True() : dateField.before(date);
    }

    public static BooleanExpression localDateMore(DatePath<LocalDate> dateField, LocalDate date) {
        return date == null ? True() : dateField.after(date);
    }

    public static BooleanExpression localDateLessOrEq(DatePath<LocalDate> dateField, LocalDate date) {
        return date == null ? True() : dateField.before(date).or(dateField.eq(date));
    }

    public static BooleanExpression localDateMoreOrEq(DatePath<LocalDate> dateField, LocalDate date) {
        return date == null ? True() : dateField.after(date).or(dateField.eq(date));
    }

    public static BooleanExpression localDateEq(DatePath<LocalDate> dateField, LocalDate date) {
        return date == null ? True() : dateField.eq(date);
    }

    public static <T extends Enum<T>> BooleanExpression enumEq(EnumPath<T> enumField, T enumValue) {
        return enumValue == null ? True() : enumField.eq(enumValue);
    }


    public static BooleanExpression True() {
        return Expressions.asBoolean(true).isTrue();
    }

    public static BooleanExpression False() {
        return Expressions.asBoolean(true).isFalse();
    }
}
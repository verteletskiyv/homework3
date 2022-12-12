1. Модифікувати завдання з попереднього блоку (у папці є перелік текстових файлів, кожен із яких є "зліпок" БД порушень правил дорожнього руху протягом певного року...) таким чином, щоб різні файли з папки завантажувалися асинхронно за допомогою пулу потоків, але загальна статистика однаково формувалася.
   Використовувати CompletableFuture і ExecutorService.
   Порівняти швидкодію програми, коли не використовується параллелизація, коли використовується 2 потоки, 4 і 8.
   Файлів в папці повинно бути 10+, їх розмір повинен бути достатнім, щоб заміри були цікавими.
   Результати порівняння прикласти коментарем до викононаго завдання.


2. Розробити класс-утиліту зі статичним методом, який приймає параметр типу Class і шлях до properties-файлу,
   створює об'єкт цього класу, наповнює його атрибути значеннями з properties-файлу (викоистовуючи сеттери) і повертає цей об'єкт.

Приклад сигнатури метода:
<code>
<br>public static <T>T loadFromProperties(Class<T> cls, Path propertiesPath)
</code>

Properties-файл має формат:
<code>
<br>stringProperty=value1<br>
numberProperty=10<br>
timeProperty=29.11.2022 18:30<br>
</code>

Метод має вміти парсити строки, цілі числа (int та Integer) і дати з часом (Instant).
Створити аннотацію @Property, за допомогою якої можна було б опціонально перезадати назву property (акщо назва атрибуту класу відрізняється від ключу property в файлі), а для полів типу дата/час задати - очікуваний формат дати.
Наприклад, клас, який ми читаємо з файлу вище, міг бі мати такі трибути:
<br>
<code>private String stringProperty;<br>
@Property(name="numberProperty")<br>
private int myNumber<br>
@Property(format="dd.MM.yyyy tt:mm")<br>
private Instant timeProperty;
</code><br>
Складені ключі (prefix.propertyKey) в цьому завданні можуть використовуватися тільки якщо ми задаємо їх в аннотації @Property(name="prefix.propertyKey").
Якщо щось розпарсии/заповнити не вдалося (не підтримується тип, неправильний формат, тощо), метод повинен кидати відповідний Exception.
Створити unit-тести для цього метода.
package com.evans.codegen;

import static java.util.function.Predicate.not;
import static org.reflections.ReflectionUtils.Methods;
import static org.reflections.util.ReflectionUtilsPredicates.withModifier;

import com.evans.codegen.file.FileGenerator;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Mustache.Visitor;
import com.samskivert.mustache.Template;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

public class UnusedVariablesFinder {

  @Test
  public void findUnusedRecordComponents()
      throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException,
          IllegalAccessException {

    Reflections reflections = new Reflections("com.evans.codegen", Scanners.SubTypes);

    List<Class<? extends FileGenerator>> fileGenerators =
        reflections.getSubTypesOf(FileGenerator.class).stream()
            .filter(not(Class::isInterface))
            .toList();

    Map<String, Set<String>> unusedVariablesByTemplateName = new HashMap<>();

    for (var generator : fileGenerators) {
      FileGenerator<?> fileGeneratorInstance = createFileGeneratorInstance(generator);

      Class<?> templateDataType = getTemplateDataType(generator);
      Set<String> possibleVariables = getPossibleVariables(templateDataType);

      // call template
      Set<String> usedVariables = findVariablesInTemplate(fileGeneratorInstance.templateName());

      // only use up to first instance of .
      Set<Object> topLevelTemplateVariables =
          usedVariables.stream().map(this::substringUpToFirstPeriod).collect(Collectors.toSet());

      Set<String> unusedVariables =
          possibleVariables.stream()
              .filter(not(topLevelTemplateVariables::contains))
              .collect(Collectors.toSet());

      if (!unusedVariables.isEmpty()) {
        unusedVariablesByTemplateName.put(fileGeneratorInstance.templateName(), unusedVariables);
      }
    }

    System.out.println(unusedVariablesByTemplateName);
  }

  private String substringUpToFirstPeriod(String variable) {
    if (variable.contains(".")) {
      return variable.substring(0, variable.indexOf("."));
    }
    return variable;
  }

  private FileGenerator<?> createFileGeneratorInstance(
      Class<? extends FileGenerator> fileGeneratorClass)
      throws NoSuchMethodException, InvocationTargetException, InstantiationException,
          IllegalAccessException {
    var constructor = fileGeneratorClass.getConstructor();

    return constructor.newInstance();
  }

  private Class<?> getTemplateDataType(Class<? extends FileGenerator> fileGeneratorClass) {
    Type[] genericInterfaces = fileGeneratorClass.getGenericInterfaces();
    ParameterizedType parameterizedType = (ParameterizedType) genericInterfaces[0];
    Type[] genericTypeArguments = parameterizedType.getActualTypeArguments();
    Type genericTypeArgument = genericTypeArguments[0];
    return (Class<?>) genericTypeArgument;
  }

  private Set<String> findVariablesInTemplate(String fileName) throws IOException {
    String templateText = Files.readString(Path.of("src/main/resources", fileName));

    Template template = Mustache.compiler().withLoader(FileGenerator.LOADER).compile(templateText);

    Set<String> variables = new HashSet<>();

    template.visit(
        new Visitor() {
          @Override
          public void visitText(String s) {}

          @Override
          public void visitVariable(String s) {
            variables.add(s);
          }

          @Override
          public boolean visitInclude(String s) {
            variables.add(s);
            return true;
          }

          @Override
          public boolean visitSection(String s) {
            variables.add(s);
            return true;
          }

          @Override
          public boolean visitInvertedSection(String s) {
            variables.add(s);
            return true;
          }
        });

    return variables.stream().filter(variable -> !variable.equals(".")).collect(Collectors.toSet());
  }

  private Set<String> getPossibleVariables(Class<?> generatorClass) {
    HashSet<String> variables = new HashSet<>();
    if (generatorClass.isRecord()) {
      RecordComponent[] recordComponents = generatorClass.getRecordComponents();

      variables.addAll(
          Arrays.stream(recordComponents)
              .map(RecordComponent::getName)
              .collect(Collectors.toSet()));

      Set<String> methods =
          ReflectionUtils.get(
              Methods.of(generatorClass)
                  .filter(not(withModifier(Modifier.STATIC)))
                  .map(Method::getName));

      variables.addAll(methods);
    }

    return variables;
  }
}

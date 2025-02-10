import { View, Text, Button, StyleSheet, TextInput } from 'react-native';
import React, { useState } from 'react';


const HomeScreen = ({ navigation }) => {
  const [text, setText] = useState('');

  return (
    <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
      <Text>Home Screen</Text>
      <TextInput
        style={styles.input}
        placeholder="User Id"
        value={text}
        onChangeText={(newText) => setText(newText)}
      />
      <Button
        title="Start liveness"
        onPress={() => {
          if (typeof navigation.navigate !== 'function') {
            console.error("navigation.navigate không phải là một function");
            return;
          }
          navigation.navigate('Liveness', { userId: text });
        }}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  input: {
    height: 40,
    borderColor: 'gray',
    borderWidth: 1,
    paddingHorizontal: 10,
    marginBottom: 20,
  },
});

export default HomeScreen;

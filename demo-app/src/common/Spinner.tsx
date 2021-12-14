import React from 'react';
import { StyleSheet, ActivityIndicator, View } from 'react-native';

const styles = StyleSheet.create({
  container: {
    position: 'absolute',
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    left: 0,
    top: 0,
    width: '100%',
    height: '100%',
  },
  horizontal: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    padding: 10,
  },
});

interface SpinnerProps {
  show: boolean;
}

const Spinner = (props: SpinnerProps) => {
  return (
    <View style={[styles.container]}>
      <ActivityIndicator animating={props.show} size="large" color="red" />
    </View>
  );
};

export default Spinner;
// SimpleModal.js
import React, { useState } from 'react';
import { View, Text, Button, Modal, StyleSheet, TouchableOpacity, ScrollView } from 'react-native';

export default function SimpleModal({
  isOpen,
  setIsOpen,
  onConfirm,
  errorMessage,
}) {
  return (
    <Modal
        animationType="slide"
        transparent={true}
        visible={isOpen}
        onRequestClose={onConfirm}
      >
        <View style={styles.modalBackground}>
          <View style={styles.modalContent}>
            <ScrollView>
              <Text style={styles.modalText}>
                {errorMessage || "An unexpected error occurred. Please try again."}
              </Text>
            </ScrollView>
            <TouchableOpacity style={styles.closeButton} onPress={onConfirm}>
              <Text style={styles.closeButtonText}>Close</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  modalBackground: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
  },
  modalContent: {
    width: 300,
    padding: 20,
    backgroundColor: 'white',
    borderRadius: 10,
    alignItems: 'center',
  },
  modalText: {
    fontSize: 18,
    marginBottom: 20,
    color: 'black',
  },
  closeButton: {
    backgroundColor: 'blue',
    padding: 10,
    borderRadius: 5,
  },
  closeButtonText: {
    color: 'white',
    fontSize: 16,
  },
});

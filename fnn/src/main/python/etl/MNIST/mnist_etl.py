import os
import struct
import numpy as np
import matplotlib.pyplot as plt
from scipy.ndimage import rotate, shift, zoom
from pathlib import Path


def read_images(file_path):
  with open(file_path, "rb") as f:
    magic, num_images, rows, cols = struct.unpack(">IIII", f.read(16))
    images = np.frombuffer(f.read(), dtype=np.uint8).reshape(num_images, rows, cols)
  return images.reshape(num_images, rows*cols)


def read_labels(file_path):
  with open(file_path, "rb") as f:
    magic, num_labels = struct.unpack(">II", f.read(8))
    labels = np.frombuffer(f.read(), dtype=np.uint8)
  return labels


def center_crop_or_pad(img, target_size=28):
    """Ensure output is exactly target_size x target_size."""
    h, w = img.shape
    pad_h = max(0, target_size - h)
    pad_w = max(0, target_size - w)

    # Pad if needed
    if pad_h > 0 or pad_w > 0:
        img = np.pad(
            img,
            ((pad_h // 2, pad_h - pad_h // 2),
             (pad_w // 2, pad_w - pad_w // 2)),
            mode='constant'
        )
    
    # Crop if needed
    h, w = img.shape
    if h > target_size:
        start = (h - target_size) // 2
        img = img[start:start+target_size, :]
    if w > target_size:
        start = (w - target_size) // 2
        img = img[:, start:start+target_size]

    return img


def apply_random_transforms(images, labels):
    transformed_images = []
    transformed_labels = []

    for img, label in zip(images, labels):
        img_2d = img.reshape(28, 28).astype(np.float32)

        # --- Random rotation ---
        angle = np.random.uniform(-90, 90)
        img_2d = rotate(img_2d, angle, reshape=False, cval=0)

        # --- Random shift ---
        shift_x = np.random.uniform(-7, 7)
        shift_y = np.random.uniform(-7, 7)
        img_2d = shift(img_2d, [shift_y, shift_x], cval=0)

        # --- Random scale ---
        scale = np.random.uniform(0.7, 1.3)
        if scale != 1.0:
            img_2d = zoom(img_2d, scale, order=1)
            img_2d = center_crop_or_pad(img_2d, 28)
        
        img_2d = np.asarray(img_2d, dtype=np.float32)

        # --- Random noise ---
        noise1 = np.random.normal(-231, 24, (28, 28))
        noise2 = np.random.normal(231, 24, (28, 28))
        img_2d = np.clip(img_2d + noise1 + noise2, 0, 255)

        transformed_images.append(img_2d.flatten())
        transformed_labels.append(label)

    return (
        np.array(transformed_images, dtype=np.uint8),
        np.array(transformed_labels)
    )

def main():
  mod_path = Path(__file__).parent
  archive = (mod_path / "../../../resources/archive/").resolve()
  #archive = "../../../resources/archive/"
  train_images_file = os.path.join(archive, "train-images.idx3-ubyte")
  train_labels_file = os.path.join(archive, "train-labels.idx1-ubyte")
  test_images_file = os.path.join(archive, "t10k-images.idx3-ubyte")
  test_labels_file = os.path.join(archive, "t10k-labels.idx1-ubyte")
  train_images = read_images(train_images_file)
  train_labels = read_labels(train_labels_file)
  test_images = read_images(test_images_file)
  test_labels = read_labels(test_labels_file)

  print("Training set:")
  print("Images shape:", train_images.shape)
  print("Labels shape:", train_labels.shape)

  print("\nTest set:")
  print("Images shape:", test_images.shape)
  print("Labels shape:", test_labels.shape)

  
  train_images_aug, train_labels_aug = apply_random_transforms(train_images, train_labels)
  test_images_aug, test_labels_aug = apply_random_transforms(test_images, test_labels)



if __name__ == "__main__":
    main()

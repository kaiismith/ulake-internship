"""
Copyright 2017-2018 Fizyr (https://fizyr.com)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
"""

from __future__ import print_function

from keras_retinanet.utils.visualization import draw_detections

from .overlap import compute_overlap
from .visualization import draw_masks,draw_mask

import numpy as np
import os

import cv2

from keras_retinanet.utils.image import read_image_bgr, preprocess_image

def _compute_ap(recall, precision):
    """ Compute the average precision, given the recall and precision curves.

    Code originally from https://github.com/rbgirshick/py-faster-rcnn.

    # Arguments
        recall:    The recall curve (list).
        precision: The precision curve (list).
    # Returns
        The average precision as computed in py-faster-rcnn.
    """
    # correct AP calculation
    # first append sentinel values at the end
    mrec = np.concatenate(([0.], recall, [1.]))
    mpre = np.concatenate(([0.], precision, [0.]))

    # compute the precision envelope
    for i in range(mpre.size - 1, 0, -1):
        mpre[i - 1] = np.maximum(mpre[i - 1], mpre[i])

    # to calculate area under PR curve, look for points
    # where X axis (recall) changes value
    i = np.where(mrec[1:] != mrec[:-1])[0]

    # and sum (\Delta recall) * prec
    ap = np.sum((mrec[i + 1] - mrec[i]) * mpre[i + 1])
    return ap


def _get_detections(generator, model, score_threshold=0.05, max_detections=100, save_path=None):
    """ Get the detections from the model using the generator.

    The result is a list of lists such that the size is:
        all_detections[num_images][num_classes] = detections[num_detections, 4 + num_classes]

    # Arguments
        generator       : The generator used to run images through the model.
        model           : The model to run on the images.
        score_threshold : The score confidence threshold to use.
        max_detections  : The maximum number of detections to use per image.
        save_path       : The path to save the images with visualized detections to.
    # Returns
        A list of lists containing the detections for each image in the generator.
    """
    all_detections = [[None for i in range(generator.num_classes())] for j in range(generator.size())]
    all_masks      = [[None for i in range(generator.num_classes())] for j in range(generator.size())]
    list_images = [None for i in range(generator.size())]

    for i in range(generator.size()):
        raw_image    = generator.load_image(i)
        image        = generator.preprocess_image(raw_image.copy())
        image, scale = generator.resize_image(image)

        image_path = generator.image_path(i)
        image_name = image_path.split('/')[-1]

        # run network
        outputs = model.predict_on_batch(np.expand_dims(image, axis=0))
        boxes  = outputs[-4]
        scores = outputs[-3]
        labels = outputs[-2]
        masks  = outputs[-1]

        # correct boxes for image scale
        boxes /= scale

        # select indices which have a score above the threshold
        indices = np.where(scores[0, :] > score_threshold)[0]

        # select those scores
        scores = scores[0][indices]

        # find the order with which to sort the scores
        scores_sort = np.argsort(-scores)[:max_detections]

        # select detections
        image_boxes      = boxes[0, indices[scores_sort], :]
        image_scores     = scores[scores_sort]
        image_labels     = labels[0, indices[scores_sort]]
        image_masks      = masks[0, indices[scores_sort], :, :, image_labels]
        image_detections = np.concatenate([image_boxes, np.expand_dims(image_scores, axis=1), np.expand_dims(image_labels, axis=1)], axis=1)

        if save_path is not None:
            # draw_annotations(raw_image, generator.load_annotations(i)[0], label_to_name=generator.label_to_name)
            draw_detections(raw_image, image_boxes, image_scores, image_labels, score_threshold=score_threshold, label_to_name=generator.label_to_name)
            draw_masks(raw_image, image_boxes.astype(int), image_masks, labels=image_labels,color=(255, 255, 0))

            cv2.imwrite(os.path.join(save_path, '{}.png'.format(image_name.split('.jpg')[0])), raw_image)

        # copy detections to all_detections
        for label in range(generator.num_classes()):
            all_detections[i][label] = image_detections[image_detections[:, -1] == label, :-1]
            all_masks[i][label]      = image_masks[image_detections[:, -1] == label, ...]
        list_images[i] = image_name

        print('{}/{}'.format(i + 1, generator.size()), end='\r')

    return all_detections, all_masks, list_images


def _get_annotations(generator,save_path=None):
    """ Get the ground truth annotations from the generator.

    The result is a list of lists such that the size is:
        all_detections[num_images][num_classes] = annotations[num_detections, 5]

    # Arguments
        generator : The generator used to retrieve ground truth annotations.
    # Returns
        A list of lists containing the annotations for each image in the generator.
    """
    all_annotations = [[None for i in range(generator.num_classes())] for j in range(generator.size())]
    all_masks       = [[None for i in range(generator.num_classes())] for j in range(generator.size())]

    for i in range(generator.size()):

        image_path = generator.image_path(i)
        image_name = image_path.split('/')[-1]


        # load the annotations
        annotations = generator.load_annotations(i)
        annotations['masks'] = np.stack(annotations['masks'], axis=0)

        if save_path is not None:
            image_path = os.path.join('/storage/cuongnc/LUNA/3d_nodule_detection/nodule_boudingbox/keras-maskrcnn_new/output',image_name.replace('.jpg','.png'))
            print('------------',image_path,image_name)
            image = read_image_bgr(image_path)
            #image        = generator.preprocess_image(raw_image.copy())
            #image, scale = generator.resize_image(image)
            b = annotations['bboxes'][0].astype(int)
            mask = annotations['masks'][0][b[1]:b[3], b[0]:b[2]]
            #print(annotations['bboxes'])
            # draw_annotations(raw_image, generator.load_annotations(i)[0], label_to_name=generator.label_to_name)
            cv2.rectangle(image,(b[0], b[1]), (b[2], b[3]), (0, 255, 0),2)
            #draw_mask(image, b, mask, annotations['labels'][0].astype(int), color=(0, 255, 0))
            cv2.imwrite(image_path, image)

        # copy detections to all_annotations
        for label in range(generator.num_classes()):

            all_annotations[i][label] = annotations['bboxes'][annotations['labels'] == label, :].copy()
            all_masks[i][label]       = annotations['masks'][annotations['labels'] == label, ..., 0].copy()

        print('{}/{}'.format(i + 1, generator.size()), end='\r')

    return all_annotations, all_masks


def evaluate(
    generator,
    model,
    iou_threshold=0.5,
    score_threshold=0.05,
    max_detections=100,
    binarize_threshold=0.5,
    save_path=None
):
    print('iou_threshold, score_threshold',iou_threshold, score_threshold)
    """ Evaluate a given dataset using a given model.

    # Arguments
        generator          : The generator that represents the dataset to evaluate.
        model              : The model to evaluate.
        iou_threshold      : The threshold used to consider when a detection is positive or negative.
        score_threshold    : The score confidence threshold to use for detections.
        max_detections     : The maximum number of detections to use per image.
        binarize_threshold : Threshold to binarize the masks with.
        save_path          : The path to save images with visualized detections to.
    # Returns
        A dict mapping class names to mAP scores.
    """
    # gather all detections and annotations
    all_detections, all_masks, list_images     = _get_detections(generator, model, score_threshold=score_threshold, max_detections=max_detections, save_path=save_path)
    all_annotations, all_gt_masks = _get_annotations(generator, save_path=save_path)
    average_precisions = {}

    # import pickle
    # pickle.dump(all_detections, open('all_detections.pkl', 'wb'))
    # pickle.dump(all_masks, open('all_masks.pkl', 'wb'))
    # pickle.dump(all_annotations, open('all_annotations.pkl', 'wb'))
    # pickle.dump(all_gt_masks, open('all_gt_masks.pkl', 'wb'))

    # process detections and annotations
    for label in range(generator.num_classes()):
        false_positives = np.zeros((0,))
        true_positives  = np.zeros((0,))
        scores          = np.zeros((0,))
        num_annotations = 0.0

        for i in range(generator.size()):
            detections           = all_detections[i][label]
            masks                = all_masks[i][label]
            annotations          = all_annotations[i][label]
            gt_masks             = all_gt_masks[i][label]
            num_annotations     += annotations.shape[0]
            detected_annotations = []

            image_name = list_images[i]
            image_path = os.path.join('/storage/cuongnc/LUNA/3d_nodule_detection/nodule_boudingbox/keras-maskrcnn_new/output',image_name.replace('.jpg','.png'))
            image = read_image_bgr(image_path)

            for d, mask in zip(detections, masks):
                box = d[:4].astype(int)
                scores = np.append(scores, d[4])

                if annotations.shape[0] == 0:
                    false_positives = np.append(false_positives, 1)
                    true_positives  = np.append(true_positives, 0)
                    continue

                # resize to fit the box
                mask = cv2.resize(mask, (box[2] - box[0], box[3] - box[1]))

                # binarize the mask
                mask = (mask > binarize_threshold).astype(np.uint8)

                # place mask in image frame
                mask_image = np.zeros_like(gt_masks[0])
                mask_image[box[1]:box[3], box[0]:box[2]] = mask
                mask = mask_image

                
                print(mask.shape, type(mask))
                print(gt_masks.shape, type(gt_masks))
                cv2.imwrite('a.jpg', mask*255)
                cv2.imwrite('b.jpg', gt_masks[0]*255)

                overlaps            = compute_overlap(np.expand_dims(mask, axis=0), gt_masks)
                assigned_annotation = np.argmax(overlaps, axis=1)
                max_overlap         = overlaps[0, assigned_annotation]

                #print('max_overlap ', max_overlap[0], image_name)
                cv2.putText(image, '{:.1f}'.format(max_overlap[0]), (box[0]-15, box[1]-15), cv2.FONT_HERSHEY_DUPLEX, 0.5, (255, 255, 255), 1)
                cv2.imwrite(image_path, image)

                if max_overlap >= iou_threshold and assigned_annotation not in detected_annotations:
                    false_positives = np.append(false_positives, 0)
                    true_positives  = np.append(true_positives, 1)
                    detected_annotations.append(assigned_annotation)
                else:
                    false_positives = np.append(false_positives, 1)
                    true_positives  = np.append(true_positives, 0)

        # no annotations -> AP for this class is 0 (is this correct?)
        if num_annotations == 0:
            average_precisions[label] = 0, 0
            continue

        print(false_positives)
        print(true_positives)
        # sort by score
        indices         = np.argsort(-scores)
        false_positives = false_positives[indices]
        true_positives  = true_positives[indices]

        # compute false positives and true positives
        false_positives = np.cumsum(false_positives)
        true_positives  = np.cumsum(true_positives)



        # compute recall and precision
        recall    = true_positives / num_annotations
        precision = true_positives / np.maximum(true_positives + false_positives, np.finfo(np.float64).eps)

        # compute average precision
        average_precision  = _compute_ap(recall, precision)
        average_precisions[label] = average_precision, num_annotations

    return average_precisions

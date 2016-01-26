addpath('./provided_code/');
load('kMeans.mat');
directoryframes = 'frames/';
directorysift = 'sift/';
allsift = dir([directorysift '/*.mat']); 
qrs = allsift(randperm(1000, 4)); % find queries to utilize 

for i = 1:4 % create histograms for 4 queries, use indices from selectRegion
    qr = [directorysift '/' qrs(i).name];
    load(qr, 'imname', 'descriptors', 'positions');
    x1 = [directoryframes '/' imname]; 
    frame = imread(x1);
    imageindices = selectRegion(frame, positions);
    distances = dist2(clustercenters, descriptors(imageindices,:)); % find min euc distances between words and descriptors for histogram
    [~,x1] = min(distances);
    histogram = histc(x1, 1:length(clustercenters)); 
    hists1(i,:) = histogram/norm(histogram); % normalize histogram
end

for i = 1:length(allsift) % create histogram for all frames in directorysift
    qr = [directorysift '/' allsift(i).name];
    load(qr, 'imname', 'descriptors');
    distances = dist2(clustercenters, descriptors); % find min euc distances between words and descriptors for histogram
    [~,x1] = min(distances);
    histogram = histc(x1, 1:length(clustercenters));
    hists2(i,:) = histogram/norm(histogram); % normalize histogram
end
products = hists1*(hists2'); % find normalized scalar product using 2 histograms 
products(isnan(products)) = 0; % remove any NaNs to avoid black frames
[~,msframes] = sort(products, 2, 'descend'); % sort according to most similar in rank order 

for j = 1:4 % iterate through to imshow queries
    qr = [directorysift '/' qrs(j).name];
    load(qr, 'imname');
    x = [directoryframes '/' imname]; 
    frame = imread(x);
    subplot(4, 6, 6*(j-1)+1);
    imshow(frame);
    for k = 1:5 % imshow all frames with same regions in subplot
        frame = [directorysift '/' allsift(msframes(j,k)).name];
        load(frame, 'imname');
        subplot(4, 6, 6*(j-1)+1+k);
        x1 = [directoryframes '/' imname]; 
        imshow(x1);
    end
end